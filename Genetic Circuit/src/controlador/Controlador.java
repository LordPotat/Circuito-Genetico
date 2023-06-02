package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelo.Modelo;
import modelo.circuitos.Circuito;
import modelo.entidades.Entidad;
import modelo.entidades.Poblacion;
import processing.core.PVector;
import vista.Vista;
import vista.panel_control.PanelControl;
import vista.ventana_grafica.Ventana;

/** 
 * Se encarga de iniciar y manipular el flujo del programa. Contiene la vista y el modelo de datos.
 * 
 * @author Alberto P�rez
 */
public class Controlador {
	

	private static final String CIRCUITO_INICIAL = "circuito1";
	
	private Vista vista;
	private Modelo modelo;
	
	/**
	 * Flag que indica si el modo autom�tico est� activado y las generaciones suceden solas
	 */
	private boolean modoAutomatico = false;
	/**
	 * Flag que indica si el proceso evolutivo est� parado hasta que se reactive
	 */
	private boolean parado = true;
	/**
	 * Almacena temporalmente el tiempo de vida actualizado
	 */
	private int tiempoVidaCache;
	
	/** 
	 * Inicia la vista con la propia instancia para que las interfaces accedan al controlador
	 * @throws InterruptedException 
	 */
	public void iniciarVista() throws InterruptedException  {
		vista = new Vista(this);
		//Espera a que de tiempo a que se cargue la ventana
		Thread.sleep(300);
		//Cuando la ventana est� correctamente iniciada se pueden iniciar los datos
		iniciarCircuito();
	}

	/** 
	 * Inicia los objetos del modelo de datos presentes en el circuito: la meta, obstaculos 
	 * Obtiene los datos almacenados en el circuito escogido para configurar sus par�metros
	 */
	public void iniciarCircuito() {
		modelo = new Modelo(this);
		Ventana ventana = vista.getVentana();
		Circuito.guardarCircuito("circuito1", ventana);
		modelo.setCircuito(Circuito.cargarCircuito(CIRCUITO_INICIAL));
		Circuito circuitoInicial = modelo.getCircuito();
		modelo.setMeta(circuitoInicial.setupMeta(ventana));
		modelo.setObstaculos(circuitoInicial.setupObstaculos(ventana));
	}

	/**
	 * Realiza todo el proceso evolutivo de la poblaci�n. Comprueba si se ha cumplido
	 * el objetivo para pararlo y mostrar la ruta �ptima. En caso contrario, debe permitir
	 * a la poblaci�n realizar su ciclo de vida hasta que acabe y tenga que evolucionar.
	 * En ese momento llamara a la funci�n con los pasos del algoritmo gen�tico necesarios
	 * @return el numero de generaciones actual de la poblaci�n
	 */
	public void manipularPoblacion() {
		Poblacion entidades = modelo.getPoblacion();
		Ventana ventana = vista.getVentana();
		PanelControl panelControl = vista.getPanelControl();
		/* Comprueba si las poblaci�n ha cumplido el objetivo y sale de la funci�n 
		 * si es el caso despu�s de mostrar la ruta �ptima, para no continuar el proceso
		 */
		if(entidades.isObjetivoCumplido()) {
			//Deshabilita el bot�n de pausa ya que no est� en el proceso ya
			panelControl.getBtnPausar().setEnabled(false);
			panelControl.setValor("Generacion", entidades.getNumGeneraciones());
			mostrarRutaOptima(entidades.getMejorEntidad());
			return;
		}
		//N�mero de frames que han pasado desde el inicio de su ciclo de vida
		int numFramesGen = ventana.getNumFramesGen();
		//Si todav�a le queda tiempo de vida a la poblaci�n, realiza un ciclo de ejecuci�n
		if(numFramesGen < tiempoVidaCache) {
			entidades.realizarCiclo();
			//En cada ciclo debe monitorizar los datos de la entidad monitorizada, si es que hay
			if(entidades.getEntidadMonitorizada() != null) {
				monitorizarEntidad(entidades.getEntidadMonitorizada());
			}
			ventana.setNumFramesGen(++numFramesGen);
		} 
		//De lo contrario, la poblaci�n debe evolucionar y reiniciar su ciclo de vida
		else {
			ventana.setNumFramesGen(0);
			entidades.evolucionar();
			/* Almacena temporalmente el valor del tiempo de vida modificado en medio del proceso,
			 * para que no intenten aplicar genes que est�n fuera de su rango y espere a que se
			 * reproduzcan para aplicarle el cambio
			 */
			tiempoVidaCache = entidades.getTiempoVida();
			/* Si el modo autom�tico no est� activado, para el proceso hasta que se pulse
			 * el bot�n de "siguiente". Si est� activado, deja que contin�e por su cuenta.
			 */
			if(!modoAutomatico) {
				parado = true; 
				//Desabilita el bot�n de pausa para que no pueda iniciar otra generaci�n por error
				vista.getPanelControl().getBtnPausar().setEnabled(false);
				/* Habilita el bot�n de "siguiente" para que pueda pasar a la siguiente generaci�n
				 * Lo hace con un delay para que se actualice correctamente el contador de generaciones
				 */
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						vista.getPanelControl().getBtnProceder().setEnabled(true);
					}
				}, 5);
			} else {
				//Si el modo autom�tico est� activado debe dejar de monitorizar s�lo
				limpiarEntidadMonitorizada();
				limpiarUltimaGeneracion();
			}
			
		}
		panelControl.setValor("Generacion", entidades.getNumGeneraciones());
		return;
	}
	
	/**
	 * Muestra en la ventana gr�fica la ruta �ptima (en el tiempo establecido) 
	 * desde el punto inicial de la poblaci�n hasta la meta
	 * @param mejorEntidad: la entidad que ha logrado el objetivo establecido 
	 */
	public void mostrarRutaOptima(Entidad mejorEntidad) {
		vista.getVentana().drawRutaOptima(mejorEntidad.getAdn().getGenes(), mejorEntidad.getTiempoObtenido());
		vista.getVentana().drawEntidad(mejorEntidad.getPosicion(), mejorEntidad.getVelocidad(), mejorEntidad.isMonitorizada());
	}
	
	/**
	 * Muestra una entidad en la ventana gr�fica en la posici�n y direcci�n actual
	 * @param entidad que se debe mostrar
	 */
	public void mostrarEntidad(Entidad entidad) {
		vista.getVentana().drawEntidad(entidad.getPosicion(), entidad.getVelocidad(), entidad.isMonitorizada());
	}
	
	/**
	 * Actualiza un campo (label) del panel de control con un nuevo valor
	 * @param <T> Tipo de valor
	 * @param label: nombre del campo que debe actualizar
	 * @param valor
	 */
	public <T> void actualizarPanel(String label, T valor) {
		vista.getPanelControl().setValor(label, valor);
	}
	
	/**
	 * Vac�a los datos del panel de control correspondientes a la �ltima generaci�n
	 */
	private void limpiarUltimaGeneracion() {
		modelo.getPoblacion().setEntidadMonitorizada(null); 
		actualizarPanel("TiempoRecordActual", 0);
		actualizarPanel("MejorAptitudActual", 0);
		actualizarPanel("MetasActual", 0);
		actualizarPanel("ColisionesActual", 0);
	}
	
	/**
	 * Determina si la posicion del rat�n en el momento de haber activado un evento de
	 * 'click' se encuentra en la 'hitbox' de alguna entidad y de ser as� comienza a 
	 * monitorizar esa entidad
	 * @param posRaton: punto en el que se ha hecho click con el rat�n
	 */
	public void seleccionarEntidad(PVector posRaton) {
		Poblacion poblacion = modelo.getPoblacion();
		//Si a�n no se ha generado la poblaci�n, no hace nad
		if(poblacion == null) {
			return;
		}
		/* Recorre todas las entidades desde la �ltima posici�n (la m�s superpuesta al haber
		 * sido la �ltima en dibujarse en pantalla). Si alguna contiene en su hitbox la
		 * posici�n del rat�n, sustituye la entidad monitorizada por ella y termina de buscar
		 */
		Entidad[] entidades = poblacion.getEntidades();
		for(int i = entidades.length - 1; i >= 0; i--) {
			if (entidades[i].contieneRaton(posRaton)) {
				entidades[i].setMonitorizada(true);
				poblacion.setEntidadMonitorizada(entidades[i]);
				break;
			}
		}
	}

	/**
	 * Actualizae en el panel de control todos los datos sobre una entidad
	 * que est� siendo monitorizada actualmente
	 * @param entidad 
	 */
	public void monitorizarEntidad(Entidad entidad) {
		actualizarPanel("Entidad", entidad.getIndice());
		//Redondeamos el valor de la distancia a dos decimales para no mostrar demasiados n�meros
		actualizarPanel("DistanciaEntidad", redondearValor(entidad.getDistancia(), 2));
		actualizarPanel("DistanciaMinEntidad", redondearValor(entidad.getDistanciaMinima(), 2));
		//Para los vectores redondeamos hasta 4 decimales ya que debe mostrar m�s precisi�n
		actualizarPanel("Posicion", "("+ redondearValor(entidad.getPosicion().x, 4) +
				", " + redondearValor(entidad.getPosicion().y, 4) + ")");
		actualizarPanel("Velocidad", "("+ redondearValor(entidad.getVelocidad().x, 4) + 
				", " + redondearValor(entidad.getVelocidad().y, 4) + ")");
		actualizarPanel("Aceleracion", "("+ redondearValor(entidad.getAceleracion().x, 4) +
				", " + redondearValor(entidad.getAceleracion().y, 4) + ")");
		actualizarPanel("TiempoEntidad", entidad.getTiempoObtenido()); 
		//La aptitud no la redondeamos porque siempre son n�meros muy peque�os
		actualizarPanel("AptitudEntidad", entidad.getAptitud());
		//Seg�n la entidad tenga determinadas flags o no, mostrar� en el estado en el que est�
		String estado = entidad.isHaChocado() ? "Chocado" : entidad.isHaLlegado() ? "Llegado" : "Activa";
		actualizarPanel("EstadoEntidad", estado);
	}
	
	/**
	 * Redondea un valor decimal hasta el n�mero de decimales indicado
	 * @param valor
	 * @param numDecimales 
	 * @return el valor redondeado
	 */
	private double redondearValor(float valor, int numDecimales) {
		double factor = Math.pow(10, numDecimales);
		return Math.round(valor * factor) / factor;
	}
	
	/**
	 * Vac�a la entidad monitorizada y sus datos del panel de control cuando su generaci�n ya no exista
	 */
	private void limpiarEntidadMonitorizada() {
		modelo.getPoblacion().setEntidadMonitorizada(null); 
		actualizarPanel("Entidad", "-");
		actualizarPanel("Posicion", "(0, 0)");
		actualizarPanel("Velocidad", "(0, 0)");
		actualizarPanel("Aceleracion", "(0, 0)");
		actualizarPanel("DistanciaEntidad", 0);
		actualizarPanel("DistanciaMinEntidad", 0);
		actualizarPanel("TiempoEntidad", 0); 
		actualizarPanel("AptitudEntidad", 0);
		actualizarPanel("EstadoEntidad", "-");
	}
	
	/**
	 * Iniciar� la poblaci�n de entidades a partir de los par�metros introducidos en el 
	 * panel de control para que de comienzo al proceso evolutivo. Una vez iniciado 
	 * cambia el bot�n de "Empezar" a un bot�n de "Siguiente" y cambiar� su comportamiento. 
	 * @author Alberto
	 */
	public class BtnEmpezarListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//Iniciamos la poblaci�n con los par�metros iniciales, incluido el punto de spawn del circuito
			modelo.setPoblacionEntidades(setupPoblacion(), modelo.getCircuito().setSpawn());
			/* Actualizamos la flag de parado para que la ventana pueda empezar a llamar a la funci�n de
			 * manipularProceso() cada frame
			 */
			parado = false; 
			/* Cambiamos el texto del bot�n a "Siguiente", y le cambiamos el action listener por otro para
			 * que se dispare un evento distinto cuando se interact�e con �l
			 */
			//Guardamos el tiempo de vida en la cache para que pueda iniciar el proceso
			tiempoVidaCache = modelo.getPoblacion().getTiempoVida();
			PanelControl panelControl = vista.getPanelControl();
			JButton btnProceder = panelControl.getBtnProceder();
			btnProceder.setText("Siguiente");
			btnProceder.addActionListener(new BtnSiguienteListener()); //a�adimos el otro listener
			btnProceder.removeActionListener(this); //eliminamos el actual
			//Deshabilitamos el bot�n hasta que se pueda avanzar de generaci�n
			btnProceder.setEnabled(false); 
			//Habilita tambi�n los dem�s botones que ahora tendr�n sentido utilizar
			panelControl.getBtnReiniciar().setEnabled(true);
			panelControl.getBtnPausar().setEnabled(true);
		}
		
		/**
		 * Establece los par�metros iniciales del proceso evolutivo de la poblaci�n a partir de
		 * los valores introducidos en el panel de control
		 * @return el mapa con los par�metros 
		 */
		private HashMap<String, Integer> setupPoblacion() {
			PanelControl panelControl = vista.getPanelControl();
			HashMap<String, Integer> poblacionParams = new HashMap<String, Integer>();
			poblacionParams.put("NumEntidades", panelControl.getTotalPoblacion());
			poblacionParams.put("TasaMutacion", panelControl.getTasaMutacion());	
			poblacionParams.put("TiempoVida", panelControl.getTiempoVida()); 
			poblacionParams.put("TiempoObjetivo", panelControl.getTiempoObjetivo());
			return poblacionParams;
		}
	}
	
	/**
	 * Actualiza la flag de parado a false para que pueda realizar el ciclo de vida de las entidades 
	 * @author Alberto
	 */
	public class BtnSiguienteListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parado = false;
			//Habilita el bot�n de pausa ya que ya no provocar�a que iniciase otro ciclo
			vista.getPanelControl().getBtnPausar().setEnabled(true);
			//Desactiva el propio bot�n hasta que se pueda pasar a la siguiente generaci�n
			vista.getPanelControl().getBtnProceder().setEnabled(false);
			limpiarEntidadMonitorizada();
			limpiarUltimaGeneracion();
		}
	}
	
	/**
	 * Actualiza la flag de parado a true para que pause el ciclo de vida de las entidades
	 * hasta reanudar pulsando el mismo bot�n (actualizando la flag de nuevo)
	 * @author Alberto
	 */
	public class BtnPausarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parado = !parado; //Invierte el estado de la flag
			PanelControl panelControl = vista.getPanelControl();
			//Actualiza el texto a "Reanudar" o "pausar" seg�n est� parado o no
			String textoBtn = (parado) ? "Reanudar" : "Pausar";
			panelControl.getBtnPausar().setText(textoBtn);
			/* Si est� parado, debe impedir que la ventana se actualice cada frame, ya
			 * que si no sobreescribir� lo que hay en pantalla y no dar� la sensaci�n
			 * de "pausa" real. Cuando no est� ya parado, permite otra vez el bucle
			 */
			Ventana ventana = vista.getVentana();
			if(parado) {
				ventana.noLoop();
			} else {
				ventana.loop();
			}
			
		}
	}
	
	/**
	 * Para el actual proceso evolutivo y restaura el panel de control al estado inicial
	 * del programa para poder iniciar de nuevo otra ejecuci�n de cero
	 * @author Alberto
	 */
	public class BtnReiniciarListener implements ActionListener {
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//Impide que la ventana siga actualizando hasta que volvamos a empezar
			parado = true; 
			/* Si se ha pausado antes el proceso, la ventana deja de actualizarse, por
			 * tanto, al reiniciar debe comprobar si lo est� haciendo y de lo contrario
			 * activar el bucle de refrescar la ventana
			 */
			Ventana ventana = vista.getVentana();
			if(!ventana.isLooping()) {
				ventana.loop();
				/* Hay que reiniciar el n�mero de frames porque si no se produce un bug
				 * en el que tras empezar de nuevo, empieza a contar desde el frame en el
				 * que estaba al pausar el proceso antes de reiniciar, rompiendo el programa
				 */
				ventana.setNumFramesGen(0); 
			}
			//Dejamos activado s�lo el bot�n de empezar
			PanelControl panelControl = vista.getPanelControl();
			panelControl.getBtnReiniciar().setEnabled(false);
			panelControl.getBtnPausar().setEnabled(false);
			panelControl.getBtnPausar().setText("Pausar");
			JButton btnProceder = panelControl.getBtnProceder();
			btnProceder.setEnabled(true);
			/* Cambiamos el bot�n de "Siguiente" por "Empezar" y sustituimos su listener
			 * por el que le corresponde ahora
			 */
			btnProceder.setText("Empezar");
			btnProceder.removeActionListener(btnProceder.getActionListeners()[0]);
			btnProceder.addActionListener(new BtnEmpezarListener());
			//Reseteamos la informaci�n mostrada en el panel de control
			reiniciarInfoPanel(panelControl);
		}
		
		/* Reinicia todos los valores de informaci�n del panel de control sobre el 
		 * anterior proceso a cero pero deja los par�metros editables sin alterar
		 * por si quiere repetir un proceso igual que antes
		 */
		private void reiniciarInfoPanel(PanelControl panelControl) {
			panelControl.setValor("TiempoRecord", 0);
			panelControl.setValor("MejorAptitud", 0);
			panelControl.setValor("Metas", 0);
			panelControl.setValor("Colisiones", 0);
			panelControl.setValor("Generacion", 0);
			limpiarEntidadMonitorizada();
			limpiarUltimaGeneracion();
		}
	}
	
	/**
	 * Activa o desactiva seg�n se marque la checkbox el modo autom�tico. Si se activa
	 * las generaciones se suceder�n s�las sin necesidad del input del usuario para
	 * continuar su ciclo de vida. Si se desactiva el usuario deber� pulsar el bot�n
	 * de "Siguiente" para avanzar de generaci�n.
	 * @author Alberto
	 */
	public class CbModoAutoListener implements ActionListener {
	
		@Override
		public void actionPerformed(ActionEvent e) {
			//Obtenemos si el checkbox que lanza el evento est� o no activado
			JCheckBox cbModoAuto = (JCheckBox) e.getSource();
			boolean activado = cbModoAuto.isSelected();
			//Activa o desactiva el modo autom�tico seg�n est� seleccionado
			modoAutomatico = activado;
			
			JButton btnProceder = vista.getPanelControl().getBtnProceder();
			/* Comprueba si el bot�n proceder es ahora mismo "Siguiente" ya que no debe
			 * modificarlo si est� en modo "Empezar", ya que solo debe comenzar una poblaci�n
			 * de esa manera.
			 */
			if(btnProceder.getText().equals("Siguiente")) {
				/* Si el bot�n "Siguiente" est� habilitado, implica que la generaci�n actual
				 * ha terminado su ciclo y que est� esperando a que se pulse para continuar.
				 * Por tanto, nos interesa que cuando activemos el modo autom�tico inicie
				 * la siguiente generaci�n directamente
				 */
				if(btnProceder.isEnabled()) {
					parado = false;
					/* Deshabilita el bot�n de "Siguiente" solo si el proceso estaba parado,
					 * para que en el siguiente ciclo ya no permita avanzar manualmente
					 */
					btnProceder.setEnabled(!activado);
				} 
			}
			
		}
	}
	
	/**
	 * Establece un nuevo valor para el par�metro de la poblaci�n de entidades, a trav�s 
	 * del cambio detectado en el spinner. Realiza las restricciones necesarias antes
	 * de modificar el par�metro deseado. Sirve para cualquiera de los spinners
	 * @author Alberto
	 */
	public class SpParamListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSpinner spinner = (JSpinner) e.getSource();
			//Obtiene el nombre del par�metro a cambiar a partir del nombre del spinner
			String param = spinner.getName(); 
			int valor = (int) spinner.getValue(); //valor cambiado en el spinner
			/* Si el spinner es el correspondiente al tiempo de vida, debe controlar que
			 * su valor nunca sea menor o igual que el tiempo objetivo, as� que en ese caso
			 * altera el valor para que est� por encima
			 */
			if(param.equals("TiempoVida")) {
				int valorSpObjetivo = (int)vista.getPanelControl().getSpTiempoObjetivo().getValue();
				if(valor <= valorSpObjetivo) {
					spinner.setValue(valorSpObjetivo + 10);
					valor = (int)spinner.getValue();
				}
			}
			/* Al principio del programa la poblaci�n no est� inicializada as� que no debe actualizar
			 * ning�n par�metro en ese caso, ya se encarga el bot�n "Empezar" de capturar los valores
			 */
			if(modelo.getPoblacion() != null) {
				//Si poblaci�n ya est� inicializada puede actualizarla sin problema
				actualizarParam(param, valor);
			}
		}

		/**
		 * Seg�n el nombre del par�metro actualiza el correspondiente de la poblaci�n
		 * con el valor nuevo
		 * @param param 
		 * @param valor
		 */
		private void actualizarParam(String param, int valor) {
			switch(param) {
				case "NumEntidades":
					modelo.getPoblacion().setNumEntidades(valor);
					break;
				case "TasaMutacion":
					modelo.getPoblacion().setTasaMutacion(valor);
					break;
				case "TiempoObjetivo":
					modelo.getPoblacion().setTiempoObjetivo(valor);
					break;
				case "TiempoVida":
					modelo.getPoblacion().setTiempoVida(valor);
					break;
			}
		}
	}
	
	public Vista getVista() {
		return vista;
	}

	public Modelo getModelo() {
		return modelo;
	}

	public boolean isIniciado() {
		return modoAutomatico;
	}

	public boolean isParado() {
		return parado;
	}

}

