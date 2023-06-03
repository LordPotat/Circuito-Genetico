package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;

import modelo.Modelo;
import modelo.circuito.Circuito;
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
	
	/**
	 * Circuito que se debe cargar al inicio del programa autom�ticamente
	 */
	private static final String CIRCUITO_INICIAL = "circuito1";
	
	/**
	 * Estado que tiene actualmente el programa
	 */
	private Estado estado;
	
	/**
	 * Indica si el programa debe ejecutarse con normalidad o limitarse a guardar un circuito nuevo
	 */
	private String modoEjecucion;
	
	/**
	 * Componente del programa que contiene la interfaz de el usuario
	 */
	private Vista vista;
	/**
	 * Componente del programa que contiene los datos almacenados necesarios
	 */
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
	 * Constructor que inicializa el controlador en el modo especificado. Si se le pasa "NORMAL",
	 * se ejecuta el programa con normalidad, pero si se le pasa "EDITOR", su funcionalidad es guardar
	 * en un archivo un nuevo circuito que se haya dise�ado y programado para siguientes ejecuciones
	 * @param modoEjecuion
	 */
	public Controlador(String modoEjecuion) {
		this.modoEjecucion = modoEjecuion;
	}
	
	/** 
	 * Inicia la vista y el modelo con la propia instancia para que puedan acceder al mismo controlador
	 * y comunicarse entre s�. Despu�s determina su flujo de ejecuci�n seg�n el modo obtenido en el
	 * constructor
	 * @throws InterruptedException 
	 */
	public void iniciarControlador() throws InterruptedException  {
		/* Como el programa se acaba de iniciar y todav�a no empieza el proceso, actualiza el estado
		 * a "en espera" para que directamente se muestre en la ventana cuando se cargue
		 */
		estado = Estado.EN_ESPERA;
		//Genera las dos ventanas que formar�n la interfaz gr�fica del usuario
		vista = new Vista(this);
		//Espera a que de tiempo a que se cargue la ventana de Processing
		Thread.sleep(300);
		//Cuando la ventana est� correctamente iniciada se pueden iniciar los datos
		modelo = new Modelo(this);
		/* Seg�n el modo de ejecuci�n, guarda un circuito en el proyecto y no contin�a haciendo nada
		 * o procede con la ejecuci�n normal iniciando el circuito por defecto
		 */
		Ventana ventana = vista.getVentana();
		ejecutarModoEditorCircuitos(ventana);
		//Se carga el circuito por defecto al inicio del programa establecido en una constante
		iniciarCircuito(CIRCUITO_INICIAL ,vista.getVentana());
	}

	/** 
	 * Si se indica el modo de ejecuci�n "EDITOR", al inicio del programa
	 * guardar� como fichero en el proyecto el �ltimo circuito que hayamos dise�ado
	 * para que se pueda seleccionar y cargar la siguiente vez que ejecute el programa.
	 * El programa no contin�a haciendo nada y termina salvo que cambiemos el valor de 
	 * esa flag, pensada solo para el desarrollador y no el usuario
	 * @param ventana gr�fica necesaria para poder crear el circuito
	 */
	private void ejecutarModoEditorCircuitos(Ventana ventana) {
		if(modoEjecucion.equals("EDITOR")) {
			/* Se le tiene que pasa el nombre del circuito con el que se nombrar� el fichero y
			 * la ventana para poder colocar elementos relativos a �sta
			 */
			Circuito.guardarCircuito("circuito3", ventana);
			//Cierra de manera segura la ventana gr�fica y termina la ejecuci�n del programa
			vista.getVentana().exit();
			System.exit(0);
		}
	}
	
	/** 
	 * Inicia los objetos del modelo de datos presentes en el circuito: la meta y obstaculos .
	 * Obtiene los datos almacenados en el fichero correspondiente al circuito pasado como
	 * argumento para poder asignar los par�metros necesarios para su inicializaci�n
	 * @param nombreCircuito: el circuito que debe cargarse 
	 * @param ventana gr�fica necesaria para poder dar contexto a los elementos
	 */
	public void iniciarCircuito(String nombreCircuito, Ventana ventana) {
		//Obtiene el circuito desde su fichero de la carpeta del proyecto
		modelo.setCircuito(Circuito.cargarCircuito(nombreCircuito));
		//Inicia la meta y obst�culos a partir de los par�metros del circuito cargado
		Circuito circuito = modelo.getCircuito();
		modelo.setMeta(circuito.setupMeta(ventana));
		modelo.setObstaculos(circuito.setupObstaculos(ventana));
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
			finalizarProcesoEvolutivo(entidades, panelControl);
			return;
		}
		//N�mero de frames que han pasado desde el inicio de su ciclo de vida
		int numFramesGen = ventana.getNumFramesGen();
		//Si todav�a le queda tiempo de vida a la poblaci�n, realiza un ciclo de ejecuci�n
		if(numFramesGen < tiempoVidaCache) {
			continuarCicloVida(entidades, ventana, numFramesGen);
		} 
		//De lo contrario, la poblaci�n debe evolucionar y reiniciar su ciclo de vida
		else {
			terminarCicloVida(entidades, ventana);
		}
		//Muestra en el panel de control la generaci�n en la que se encuentra al acabar el ciclo
		panelControl.setValor("Generacion", entidades.getNumGeneraciones());
		return;
	}
	
	/**
	 * Ordena a las entidades que prosigan su ejecuci�n y muestra y actualiza la informaci�n
	 * que considere necesaria 
	 * @param entidades que deben proceder con su ciclo de vida
	 * @param ventana cuyo contador de frames debe incrementarse
	 * @param numFramesGen numero de frames que llevan vivos las entidades
	 */
	private void continuarCicloVida(Poblacion entidades, Ventana ventana, int numFramesGen) {
		entidades.realizarCiclo();
		//En cada ciclo debe monitorizar los datos de la entidad monitorizada, si es que hay
		if(entidades.getEntidadMonitorizada() != null) {
			monitorizarEntidad(entidades.getEntidadMonitorizada());
		}
		//Incrementa el contador de frames para que las entidades actu�n seg�n les toca
		ventana.setNumFramesGen(++numFramesGen);
	}
	
	/**
	 * Realiza los �ltimos pasos en el ciclo de vida de la poblaci�n para que el proceso
	 * evolutivo contin�e generando una nueva generaci�n. Despu�s dicta a los componentes
	 * de la vista lo que deben hacer como preparaci�n para la siguiente generaci�n
	 * @param entidades cuyo ciclo de vida termina y deben evolucionar en una generaci�n nueva
	 * @param ventana cuyo contador de frames debe reiniciarse para la siguiente generaci�n
	 */
	private void terminarCicloVida(Poblacion entidades, Ventana ventana) {
		ventana.setNumFramesGen(0);
		entidades.evolucionar();
		/* Almacena temporalmente el valor del tiempo de vida modificado en medio del proceso,
		 * para que no intenten aplicar genes que est�n fuera de su rango y espere a que se
		 * reproduzcan para aplicarle el cambio
		 */
		tiempoVidaCache = entidades.getTiempoVida();
		/* Si el modo autom�tico no est� activado y todav�a no se ha cumplido el objetivo, 
		 * para el proceso hasta que se pulse el bot�n de "siguiente". Si est� activado,
		 * deja que contin�e por su cuenta.
		 */
		if(!modoAutomatico && !entidades.isObjetivoCumplido()) {
			detenerProceso(); 
		} else {
			//Si el modo autom�tico est� activado debe dejar de monitorizar s�lo
			limpiarEntidadMonitorizada();
			limpiarUltimaGeneracion();
		}
	}
	
	/**
	 * Para el proceso evolutivo hasta que el usuario de la orden de reanudarlo y
	 * realiza los cambios en el panel de control para preparar la siguiente acci�n
	 */
	private void detenerProceso() {
		/* Actualiza el estado a "en espera" ya que se ha parado el proceso evolutivo 
		 * hasta que pulsemos el bot�n de "siguiente" */
		estado = Estado.EN_ESPERA;
		parado = true; 
		//Desabilita el bot�n de pausa para que no pueda iniciar otra generaci�n por error
		vista.getPanelControl().getBtnPausar().setEnabled(false);
		/* Habilita el bot�n de "siguiente" para que pueda pasar a la siguiente generaci�n
		 * Lo hace con un delay para que se actualice correctamente el contador de generaciones */
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				vista.getPanelControl().getBtnProceder().setEnabled(true);
			}
		}, 5);
	}
	
	/**
	 * Muestra la informaci�n que debe aparecer cuando el proceso evolutivo ha cumplido
	 * su objetivo, como la ruta �ptima alcanzada en la ventana
	 * @param entidades: poblaci�n de donde obtiene la entidad y datos a mostrar
	 * @param panelControl 
	 */
	private void finalizarProcesoEvolutivo(Poblacion entidades, PanelControl panelControl) {
		//Actualiza el estado a "finalizado" ya que el proceso evolutivo ha terminado
		estado = Estado.FINALIZADO;
		//Deshabilita el bot�n de pausa ya que no est� en el proceso ya
		panelControl.getBtnPausar().setEnabled(false);
		panelControl.setValor("Generacion", entidades.getNumGeneraciones());
		mostrarRutaOptima(entidades.getMejorEntidad());
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
	 * Muestra en pantalla s�lo aquellas entidades que no hayan chocado. Se llama cuando
	 * se pausa la ejecuci�n para que las entidades permanezcan dibujadas y no desaparezcan
	 * hasta reanudar.
	 */
	public void mostrarEntidadesActivas() {
		for(Entidad entidad : modelo.getPoblacion().getEntidades()) {
			if(!entidad.isHaChocado()) {
				mostrarEntidad(entidad);
			}
		}
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
	 * Actualiza en el panel de control todos los datos sobre una entidad
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
			//Actualiza el estado a "realizando ciclo" ya que se empieza a ejecutar el proceso evolutivo
			estado = Estado.REALIZANDO_CICLO;
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
			/* Deshabilita el selector de circuitos hasta que se reinicie al estado inicial
			 * ya que cambiar el circuito en mitad del proceso es contraproducente
			 */
			panelControl.getcBoxCircuito().setEnabled(false);
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
			//Actualiza el estado a "realizando ciclo" ya que se reanuda el proceso evolutivo
			estado = Estado.REALIZANDO_CICLO;
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
			/* Si se ha pausado la ejecuci�n, actualiza el estado a "pausado", y si se ha
			 * reanudado, se actualiza a "realizando ciclo */
			estado = (parado) ? Estado.PAUSADO : Estado.REALIZANDO_CICLO;
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
			//Actualiza el estado a "en espera" ya que se ha dejado de ejecutar el proceso evolutivo
			estado = Estado.EN_ESPERA;
			//Impide que la ventana siga actualizando hasta que volvamos a empezar
			parado = true; 
			Ventana ventana = vista.getVentana();
			/* Hay que reiniciar el n�mero de frames porque si no se produce un bug
			 * en el que tras empezar de nuevo, empieza a contar desde el frame en el
			 * que estaba al pausar el proceso antes de reiniciar, rompiendo el programa
			 */
			ventana.setNumFramesGen(0); 
			/* Dejamos activado s�lo el bot�n de empezar y el selector de circuitos, que
			 * ahora no tendr�a problema para poder cargar otro distinto al haber empezado
			 * otro proceso
			 */
			PanelControl panelControl = vista.getPanelControl();
			panelControl.getcBoxCircuito().setEnabled(true);
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
	 * Cierra el programa sin importar el estado en el que se encuentre
	 * @author Alberto
	 */
	public class BtnSalirListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			vista.getVentana().exit();
			System.exit(0);
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
	
	/**
	 * Cambia el circuito cargando el fichero correspondiente a la opci�n que se ha 
	 * seleccionado en el combo box y reestableciendo los par�metros necesarios en los
	 * elementos del proceso como la meta, obst�culos y la poblaci�n a partir del circuito.
	 * En el siguiente frame se actualizara el circuito mostrado en la ventana gr�fica.
	 * @author Alberto
	 */
	public class CboxCircuitoListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			//Solo se activa si se selecciona una opci�n distinta a la actual
			if(e.getStateChange() == ItemEvent.SELECTED) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cBoxCircuito = (JComboBox<String>) e.getSource();
				//Obtiene el nombre del circuito a partir del item seleccionado
				String circuitoSeleccionado = (String) cBoxCircuito.getSelectedItem();
				/* Inicia los elementos (meta y obst�culos) del circuito tras cargar el ficheros
				 * con el nombre del circuito seleccionado
				 */
				iniciarCircuito(circuitoSeleccionado, vista.getVentana());
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

	public Estado getEstado() {
		return estado;
	}

}

