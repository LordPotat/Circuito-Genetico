package controlador;

import java.util.Timer;
import java.util.TimerTask;
import modelo.Modelo;
import modelo.circuito.Circuito;
import modelo.entidades.Poblacion;
import vista.PanelControl;
import vista.Ventana;
import vista.Vista;

/** 
 * Se encarga de iniciar y manipular el flujo del programa. Contiene la vista y el modelo de datos.
 * Todo el proceso evolutivo que lleva a cabo el programa y la manipulación de las entidades se realiza
 * en este controlador, así como la obtención de los circuitos con los que interactúan
 * @author Alberto Pérez
 */
public class Controlador {
	
	/**
	 * Circuito que se debe cargar al inicio del programa automáticamente
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
	 * Flag que indica si el modo automático está activado y las generaciones suceden solas
	 */
	private boolean modoAutomatico = false;
	/**
	 * Flag que indica si el proceso evolutivo está parado hasta que se reactive
	 */
	private boolean parado = true;
	/**
	 * Almacena temporalmente el tiempo de vida actualizado
	 */
	private int tiempoVidaCache;
	/**
	 * Subcontrolador que se encarga de gestionar todos los eventos desencadenados en la vista
	 */
	private ControladorEventos controladorEventos;
	/**
	 * Subcontrolador que se encarga de gestionar la visualizacion de los datos en la vista
	 */
	private Visualizador visualizador;
	
	/**
	 * Constructor que inicializa el controlador en el modo especificado. Si se le pasa "NORMAL",
	 * se ejecuta el programa con normalidad, pero si se le pasa "EDITOR", su funcionalidad es guardar
	 * en un archivo un nuevo circuito que se haya diseñado y programado para siguientes ejecuciones
	 * @param modoEjecuion
	 */
	public Controlador(String modoEjecuion) {
		this.modoEjecucion = modoEjecuion;
	}
	
	/** 
	 * Inicia la vista y el modelo con la propia instancia para que puedan acceder al mismo controlador
	 * y comunicarse entre sí. Después determina su flujo de ejecución según el modo obtenido en el
	 * constructor
	 * @throws InterruptedException 
	 */
	public void iniciarControlador() throws InterruptedException  {
		/* Como el programa se acaba de iniciar y todavía no empieza el proceso, actualiza el estado
		 * a "en espera" para que directamente se muestre en la ventana cuando se cargue
		 */
		estado = Estado.EN_ESPERA;
		//Genera las dos ventanas que formarán la interfaz gráfica del usuario
		vista = new Vista(this);
		//Espera a que de tiempo a que se cargue la ventana de Processing
		Thread.sleep(300);
		//Cuando la ventana esté correctamente iniciada se pueden iniciar los datos
		modelo = new Modelo(this);
		/* Según el modo de ejecución, guarda un circuito en el proyecto y no continúa haciendo nada
		 * o procede con la ejecución normal iniciando el circuito por defecto
		 */
		Ventana ventana = vista.getVentana();
		/* Si se indica el modo de ejecución "EDITOR", al inicio del programa, llama al gestor de
		 * circuitos para que ejecute el modo editor de circuitos, que directamente terminará
		 * la ejecución prematuramente al guardar el circuito. Si el modo de ejecución es "NORMAL",
		 * procede con la ejecución estándar del programa cargando un circuito en la ventana
		 */
		if(modoEjecucion.equals("EDITOR")) {
			ejecutarModoEditorCircuitos(ventana);
		} else {
			//Se carga el circuito por defecto al inicio del programa establecido en una constante
			iniciarCircuito(CIRCUITO_INICIAL ,vista.getVentana());
		}
		//Inicia los controladores encargados de manejar áreas más concretas
		iniciarSubcontroladores();
	}

	/**
	 * Inicia los controladores contenidos bajo el controlador principal para modularizar el 
	 * comportamiento del programa
	 */
	private void iniciarSubcontroladores() {
		//Inicia el controlador encargado de los eventos de la interfaz
		controladorEventos = new ControladorEventos(this, modelo, vista);
		//Le asigna los eventos a los listeners de los componentes del panel de control
		vista.getPanelControl().asignarEventos();
		visualizador = new Visualizador(vista, modelo);
	}
	
	/** 
	 * Guarda como fichero en el proyecto el último circuito que hayamos diseñado
	 * para que se pueda seleccionar y cargar la siguiente vez que ejecute el programa.
	 * El programa no continúa haciendo nada y termina 
	 * @param ventana gráfica necesaria para poder crear el circuito
	 */
	private void ejecutarModoEditorCircuitos(Ventana ventana) {
		/* Se le tiene que pasa el nombre del circuito con el que se nombrará el fichero y
		 * la ventana para poder colocar elementos relativos a ésta
		 */
		Circuito.guardarCircuito("circuito3", ventana);
		//Cierra de manera segura la ventana gráfica y termina la ejecución del programa
		vista.getVentana().exit();
		System.exit(0);
	}
	
	/** 
	 * Inicia los objetos del modelo de datos presentes en el circuito: la meta y obstaculos .
	 * Obtiene los datos almacenados en el fichero correspondiente al circuito pasado como
	 * argumento para poder asignar los parámetros necesarios para su inicialización
	 * @param nombreCircuito: el circuito que debe cargarse 
	 * @param ventana gráfica necesaria para poder dar contexto a los elementos
	 */
	void iniciarCircuito(String nombreCircuito, Ventana ventana) {
		//Obtiene el circuito desde su fichero de la carpeta del proyecto
		modelo.setCircuito(Circuito.cargarCircuito(nombreCircuito));
		//Inicia la meta y obstáculos a partir de los parámetros del circuito cargado
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
			finalizarProcesoEvolutivo(entidades, panelControl);
			return;
		}
		//Número de frames que han pasado desde el inicio de su ciclo de vida
		int numFramesGen = ventana.getNumFramesGen();
		//Si todavía le queda tiempo de vida a la población, realiza un ciclo de ejecución
		if(numFramesGen < tiempoVidaCache) {
			continuarCicloVida(entidades, ventana, numFramesGen);
		} 
		//De lo contrario, la población debe evolucionar y reiniciar su ciclo de vida
		else {
			terminarCicloVida(entidades, ventana);
		}
		//Muestra en el panel de control la generación en la que se encuentra al acabar el ciclo
		panelControl.setValor("Generacion", entidades.getNumGeneraciones());
	}
	
	/**
	 * Ordena a las entidades que prosigan su ejecución y muestra y actualiza la información
	 * que considere necesaria 
	 * @param entidades que deben proceder con su ciclo de vida
	 * @param ventana cuyo contador de frames debe incrementarse
	 * @param numFramesGen numero de frames que llevan vivos las entidades
	 */
	private void continuarCicloVida(Poblacion entidades, Ventana ventana, int numFramesGen) {
		entidades.realizarCiclo();
		//En cada ciclo debe monitorizar los datos de la entidad monitorizada, si es que hay
		if(entidades.getEntidadMonitorizada() != null) {
			visualizador.monitorizarEntidad(entidades.getEntidadMonitorizada());
		}
		//Incrementa el contador de frames para que las entidades actuén según les toca
		ventana.setNumFramesGen(++numFramesGen);
	}
	
	/**
	 * Realiza los últimos pasos en el ciclo de vida de la población para que el proceso
	 * evolutivo continúe generando una nueva generación. Después dicta a los componentes
	 * de la vista lo que deben hacer como preparación para la siguiente generación
	 * @param entidades cuyo ciclo de vida termina y deben evolucionar en una generación nueva
	 * @param ventana cuyo contador de frames debe reiniciarse para la siguiente generación
	 */
	private void terminarCicloVida(Poblacion entidades, Ventana ventana) {
		ventana.setNumFramesGen(0);
		entidades.evolucionar();
		/* Almacena temporalmente el valor del tiempo de vida modificado en medio del proceso,
		 * para que no intenten aplicar genes que están fuera de su rango y espere a que se
		 * reproduzcan para aplicarle el cambio
		 */
		tiempoVidaCache = entidades.getTiempoVida();
		/* Si el modo automático no está activado y todavía no se ha cumplido el objetivo, 
		 * para el proceso hasta que se pulse el botón de "siguiente". Si está activado,
		 * deja que continúe por su cuenta.
		 */
		if(!modoAutomatico && !entidades.isObjetivoCumplido()) {
			detenerProceso(); 
		} else if (modoAutomatico && !entidades.isObjetivoCumplido()) {
			/* Si el modo automático está activado debe dejar de monitorizar sólo en caso
			 * de que no se haya cumplido el objetivo, si se ha cumplido mantiene la info*/
			visualizador.limpiarEntidadMonitorizada();
			visualizador.limpiarUltimaGeneracion();
		}
	}
	
	/**
	 * Para el proceso evolutivo hasta que el usuario de la orden de reanudarlo y
	 * realiza los cambios en el panel de control para preparar la siguiente acción
	 */
	private void detenerProceso() {
		/* Actualiza el estado a "en espera" ya que se ha parado el proceso evolutivo 
		 * hasta que pulsemos el botón de "siguiente" */
		estado = Estado.EN_ESPERA;
		parado = true; 
		//Desabilita el botón de pausa para que no pueda iniciar otra generación por error
		vista.getPanelControl().getBtnPausar().setEnabled(false);
		/* Habilita el botón de "siguiente" para que pueda pasar a la siguiente generación
		 * Lo hace con un delay para que se actualice correctamente el contador de generaciones */
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				vista.getPanelControl().getBtnProceder().setEnabled(true);
			}
		}, 5);
	}
	
	/**
	 * Muestra la información que debe aparecer cuando el proceso evolutivo ha cumplido
	 * su objetivo, como la ruta óptima alcanzada en la ventana
	 * @param entidades: población de donde obtiene la entidad y datos a mostrar
	 * @param panelControl 
	 */
	private void finalizarProcesoEvolutivo(Poblacion entidades, PanelControl panelControl) {
		//Actualiza el estado a "finalizado" ya que el proceso evolutivo ha terminado
		estado = Estado.FINALIZADO;
		//Deshabilita el botón de pausa ya que no está en el proceso ya
		panelControl.getBtnPausar().setEnabled(false);
		panelControl.setValor("Generacion", entidades.getNumGeneraciones());
		visualizador.mostrarRutaOptima(entidades.getMejorEntidad());
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

	public void setModoAutomatico(boolean modoAutomatico) {
		this.modoAutomatico = modoAutomatico;
	}

	public void setParado(boolean parado) {
		this.parado = parado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public void setTiempoVidaCache(int tiempoVidaCache) {
		this.tiempoVidaCache = tiempoVidaCache;
	}

	public ControladorEventos getControladorEventos() {
		return controladorEventos;
	}

	public boolean isModoAutomatico() {
		return modoAutomatico;
	}

	public Visualizador getVisualizador() {
		return visualizador;
	}

}

