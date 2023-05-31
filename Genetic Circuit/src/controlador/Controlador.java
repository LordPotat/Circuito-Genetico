package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import modelo.Modelo;
import modelo.circuitos.Circuito;
import modelo.entidades.Entidad;
import modelo.entidades.Poblacion;
import vista.Vista;
import vista.panel_control.PanelControl;
import vista.ventana_grafica.Ventana;

/** 
 * Se encarga de iniciar y manipular el flujo del programa. Contiene la vista y el modelo de datos.
 * 
 * @author Alberto Pérez
 */
public class Controlador {
	
	private Vista vista;
	private Modelo modelo;
	
	/**
	 * Flag que indica si el modo automático está activado y las generaciones suceden solas
	 */
	private boolean modoAutomatico = false;
	/**
	 * Flag que indica si el proceso evolutivo está parado hasta que se reactive
	 */
	private boolean parado = true;
	
	/** 
	 * Inicia la vista con la propia instancia para que las interfaces accedan al controlador
	 * @throws InterruptedException 
	 */
	public void iniciarVista() throws InterruptedException  {
		vista = new Vista(this);
		//Espera a que de tiempo a que se cargue la ventana
		Thread.sleep(300);
		//Cuando la ventana esté correctamente iniciada se pueden iniciar los datos
		iniciarCircuito();
	}

	/** 
	 * Inicia los objetos del modelo de datos presentes en el circuito: la meta, obstaculos 
	 * Obtiene los datos almacenados en el circuito escogido para configurar sus parámetros
	 */
	public void iniciarCircuito() {
		modelo = new Modelo(this);
		Ventana ventana = vista.getVentana();
		Circuito circuito = modelo.getCircuito();
		modelo.setMeta(circuito.setupMeta(ventana));
		modelo.setObstaculos(circuito.setupObstaculos(ventana));
	}

	/**
	 * Realiza todo el proceso evolutivo de la población. Comprueba si se ha cumplido
	 * el objetivo para pararlo y mostrar la ruta óptima. En caso contrario, debe permitir
	 * a la población realizar su ciclo de vida hasta que acabe y tenga que evolucionar.
	 * En ese momento llamara a la función con los pasos del algoritmo genético necesarios
	 * @return el numero de generaciones actual de la población
	 */
	
	public void manipularPoblacion() {
		Poblacion entidades = modelo.getPoblacion();
		Ventana ventana = vista.getVentana();
		PanelControl panelControl = vista.getPanelControl();
		/* Comprueba si las población ha cumplido el objetivo y sale de la función 
		 * si es el caso después de mostrar la ruta óptima, para no continuar el proceso
		 */
		if(entidades.isObjetivoCumplido()) {
			
			panelControl.setValor("Generacion", entidades.getNumGeneraciones());
			mostrarRutaOptima(entidades.getMejorEntidad());
			return;
		}
		//Número de frames que han pasado desde el inicio de su ciclo de vida
		int numFramesGen = ventana.getNumFramesGen();
		//Si todavía le queda tiempo de vida a la población, realiza un ciclo de ejecución
		if(numFramesGen < entidades.getTiempoVida()) {
			entidades.realizarCiclo();
			ventana.setNumFramesGen(++numFramesGen);
		} 
		//De lo contrario, la población debe evolucionar y reiniciar su ciclo de vida
		else {
			ventana.setNumFramesGen(0);
			entidades.evolucionar();
			/* Si el modo automático no está activado, para el proceso hasta que se pulse
			 * el botón de "siguiente". Si está activado, deja que continúe por su cuenta.
			 */
			if(!modoAutomatico) {
				parado = true; 
				//Desabilita el botón de pausa para que no pueda iniciar otra generación por error
				vista.getPanelControl().getBtnPausar().setEnabled(false);
				/* Habilita el botón de "siguiente" para que pueda pasar a la siguiente generación
				 * Lo hace con un delay para que se actualice correctamente el contador de generaciones
				 */
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						vista.getPanelControl().getBtnProceder().setEnabled(true);
					}
				}, 5);
			}
			
		}
		panelControl.setValor("Generacion", entidades.getNumGeneraciones());
		return;
	}
	
	/**
	 * Muestra en la ventana gráfica la ruta óptima (en el tiempo establecido) 
	 * desde el punto inicial de la población hasta la meta
	 * @param mejorEntidad: la entidad que ha logrado el objetivo establecido 
	 */
	public void mostrarRutaOptima(Entidad mejorEntidad) {
		vista.getVentana().drawRutaOptima(mejorEntidad.getAdn().getGenes(), mejorEntidad.getTiempoObtenido());
		vista.getVentana().drawEntidad(mejorEntidad.getPosicion(), mejorEntidad.getVelocidad());
	}
	
	/**
	 * Muestra una entidad en la ventana gráfica en la posición y dirección actual
	 * @param entidad que se debe mostrar
	 */
	public void mostrarEntidad(Entidad entidad) {
		vista.getVentana().drawEntidad(entidad.getPosicion(), entidad.getVelocidad());
	}
	
	public <T> void actualizarPanel(String label, T valor) {
		vista.getPanelControl().setValor(label, valor);
	}
	
	/**
	 * Implementa el evento desencadenado al pulsar el botón de empezar. se iniciará la población 
	 * de entidades a partir de los parámetros introducidos en el panel de control para que de
	 * comienzo al proceso evolutivo. Una vez iniciado cambia el botón de "Empezar" a un botón
	 * de "Siguiente" y cambiará su comportamiento. 
	 * @author Alberto
	 */
	public class BtnEmpezarListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//Iniciamos la población con los parámetros iniciales, incluido el punto de spawn del circuito
			modelo.setPoblacionEntidades(setupPoblacion(), modelo.getCircuito().setSpawn(vista.getVentana()));
			/* Actualizamos la flag de parado para que la ventana pueda empezar a llamar a la función de
			 * manipularProceso() cada frame
			 */
			parado = false; 
			/* Cambiamos el texto del botón a "Siguiente", y le cambiamos el action listener por otro para
			 * que se dispare un evento distinto cuando se interactúe con él
			 */
			PanelControl panelControl = vista.getPanelControl();
			JButton btnProceder = panelControl.getBtnProceder();
			btnProceder.setText("Siguiente");
			btnProceder.addActionListener(new BtnSiguienteListener()); //añadimos el otro listener
			btnProceder.removeActionListener(this); //eliminamos el actual
			//Deshabilitamos el botón hasta que se pueda avanzar de generación
			btnProceder.setEnabled(false); 
			//Habilita también los demás botones que ahora tendrán sentido utilizar
			panelControl.getBtnReiniciar().setEnabled(true);
			panelControl.getBtnPausar().setEnabled(true);
		}
		
		/**
		 * Establece los parámetros iniciales del proceso evolutivo de la población a partir de
		 * los valores introducidos en el panel de control
		 * @return el mapa con los parámetros 
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
	 * Implementa el evento desencadenado por el botón "Siguiente". Actualiza la flag
	 * de parado a false para que pueda realizar el ciclo de vida de las entidades
	 * @author Alberto
	 */
	public class BtnSiguienteListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parado = false;
			//Habilita el botón de pausa ya que ya no provocaría que iniciase otro ciclo
			vista.getPanelControl().getBtnPausar().setEnabled(true);
			//Desactiva el propio botón hasta que se pueda pasar a la siguiente generación
			vista.getPanelControl().getBtnProceder().setEnabled(false);
		}
	}
	
	/**
	 * Implementa el evento desencadenado por el botón "Pausar". Actualiza la flag
	 * de parado a true para que pause el ciclo de vida de las entidades hasta reanudar
	 * pulsando el mismo botón (actualizando la flag de nuevo)
	 * @author Alberto
	 */
	public class BtnPausarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parado = !parado; //Invierte el estado de la flag
			PanelControl panelControl = vista.getPanelControl();
			//Actualiza el texto a "Reanudar" o "pausar" según está parado o no
			String textoBtn = (parado) ? "Reanudar" : "Pausar";
			panelControl.getBtnPausar().setText(textoBtn);
			/* Si está parado, debe impedir que la ventana se actualice cada frame, ya
			 * que si no sobreescribirá lo que hay en pantalla y no dará la sensación
			 * de "pausa" real. Cuando no esté ya parado, permite otra vez el bucle
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
	 * del programa para poder iniciar de nuevo otra ejecución de cero
	 * @author Alberto
	 */
	public class BtnReiniciarListener implements ActionListener {
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//Impide que la ventana siga actualizando hasta que volvamos a empezar
			parado = true; 
			/* Si se ha pausado antes el proceso, la ventana deja de actualizarse, por
			 * tanto, al reiniciar debe comprobar si lo está haciendo y de lo contrario
			 * activar el bucle de refrescar la ventana
			 */
			Ventana ventana = vista.getVentana();
			if(!ventana.isLooping()) {
				ventana.loop();
			}
			//Dejamos activado sólo el botón de empezar
			PanelControl panelControl = vista.getPanelControl();
			panelControl.getBtnReiniciar().setEnabled(false);
			panelControl.getBtnPausar().setEnabled(false);
			JButton btnProceder = panelControl.getBtnProceder();
			btnProceder.setEnabled(true);
			/* Cambiamos el botón de "Siguiente" por "Empezar" y sustituimos su listener
			 * por el que le corresponde ahora
			 */
			btnProceder.setText("Empezar");
			btnProceder.removeActionListener(btnProceder.getActionListeners()[0]);
			btnProceder.addActionListener(new BtnEmpezarListener());
			//Reseteamos la información mostrada en el panel de control
			reiniciarInfoPanel(panelControl);
		}
		
		/* Reinicia todos los valores de información del panel de control sobre el 
		 * anterior proceso a cero pero deja los parámetros editables sin alterar
		 * por si quiere repetir un proceso igual que antes
		 */
		private void reiniciarInfoPanel(PanelControl panelControl) {
			panelControl.setValor("TiempoRecord", 0);
			panelControl.setValor("MejorAptitud", 0);
			panelControl.setValor("Metas", 0);
			panelControl.setValor("Colisiones", 0);
			panelControl.setValor("Generacion", 0);
		}
	}
	
	/**
	 * Activa o desactiva según se marque la checkbox el modo automático. Si se activa
	 * las generaciones se sucederán sólas sin necesidad del input del usuario para
	 * continuar su ciclo de vida. Si se desactiva el usuario deberá pulsar el botón
	 * de "Siguiente" para avanzar de generación.
	 * @author Alberto
	 */
	public class CbModoAutoListener implements ActionListener {
	
		@Override
		public void actionPerformed(ActionEvent e) {
			//Obtenemos si el checkbox que lanza el evento está o no activado
			JCheckBox cbModoAuto = (JCheckBox) e.getSource();
			boolean activado = cbModoAuto.isSelected();
			//Activa o desactiva el modo automático según esté seleccionado
			modoAutomatico = activado;
			
			JButton btnProceder = vista.getPanelControl().getBtnProceder();
			/* Comprueba si el botón proceder es ahora mismo "Siguiente" ya que no debe
			 * modificarlo si está en modo "Empezar, ya que solo debe comenzar una población
			 * de esa manera.
			 */
			if(btnProceder.getText().equals("Siguiente")) {
				/* Si el botón "Siguiente" está habilitado, implica que la generación actual
				 * ha terminado su ciclo y que está esperando a que se pulse para continuar.
				 * Por tanto, nos interesa que cuando activemos el modo automático inicie
				 * la siguiente generación directamente
				 */
				if(btnProceder.isEnabled()) {
					parado = false;
					/* Deshabilita el botón de "Siguiente" solo si el proceso estaba parado,
					 * para que en el siguiente ciclo ya no permita avanzar manualmente
					 */
					btnProceder.setEnabled(!activado);
				} 
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

