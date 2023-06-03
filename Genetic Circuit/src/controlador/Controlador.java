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
		ejecutarModoEditorCircuitos(ventana);
		//Se carga el circuito por defecto al inicio del programa establecido en una constante
		iniciarCircuito(CIRCUITO_INICIAL ,vista.getVentana());
	}

	/** 
	 * Si se indica el modo de ejecución "EDITOR", al inicio del programa
	 * guardará como fichero en el proyecto el último circuito que hayamos diseñado
	 * para que se pueda seleccionar y cargar la siguiente vez que ejecute el programa.
	 * El programa no continúa haciendo nada y termina salvo que cambiemos el valor de 
	 * esa flag, pensada solo para el desarrollador y no el usuario
	 * @param ventana gráfica necesaria para poder crear el circuito
	 */
	private void ejecutarModoEditorCircuitos(Ventana ventana) {
		if(modoEjecucion.equals("EDITOR")) {
			/* Se le tiene que pasa el nombre del circuito con el que se nombrará el fichero y
			 * la ventana para poder colocar elementos relativos a ésta
			 */
			Circuito.guardarCircuito("circuito3", ventana);
			//Cierra de manera segura la ventana gráfica y termina la ejecución del programa
			vista.getVentana().exit();
			System.exit(0);
		}
	}
	
	/** 
	 * Inicia los objetos del modelo de datos presentes en el circuito: la meta y obstaculos .
	 * Obtiene los datos almacenados en el fichero correspondiente al circuito pasado como
	 * argumento para poder asignar los parámetros necesarios para su inicialización
	 * @param nombreCircuito: el circuito que debe cargarse 
	 * @param ventana gráfica necesaria para poder dar contexto a los elementos
	 */
	public void iniciarCircuito(String nombreCircuito, Ventana ventana) {
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
		return;
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
			monitorizarEntidad(entidades.getEntidadMonitorizada());
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
		} else {
			//Si el modo automático está activado debe dejar de monitorizar sólo
			limpiarEntidadMonitorizada();
			limpiarUltimaGeneracion();
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
		mostrarRutaOptima(entidades.getMejorEntidad());
	}
	
	/**
	 * Muestra en la ventana gráfica la ruta óptima (en el tiempo establecido) 
	 * desde el punto inicial de la población hasta la meta
	 * @param mejorEntidad: la entidad que ha logrado el objetivo establecido 
	 */
	public void mostrarRutaOptima(Entidad mejorEntidad) {
		vista.getVentana().drawRutaOptima(mejorEntidad.getAdn().getGenes(), mejorEntidad.getTiempoObtenido());
		vista.getVentana().drawEntidad(mejorEntidad.getPosicion(), mejorEntidad.getVelocidad(), mejorEntidad.isMonitorizada());
	}
	
	/**
	 * Muestra una entidad en la ventana gráfica en la posición y dirección actual
	 * @param entidad que se debe mostrar
	 */
	public void mostrarEntidad(Entidad entidad) {
		vista.getVentana().drawEntidad(entidad.getPosicion(), entidad.getVelocidad(), entidad.isMonitorizada());
	}
	
	/**
	 * Muestra en pantalla sólo aquellas entidades que no hayan chocado. Se llama cuando
	 * se pausa la ejecución para que las entidades permanezcan dibujadas y no desaparezcan
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
	 * Vacía los datos del panel de control correspondientes a la última generación
	 */
	private void limpiarUltimaGeneracion() {
		modelo.getPoblacion().setEntidadMonitorizada(null); 
		actualizarPanel("TiempoRecordActual", 0);
		actualizarPanel("MejorAptitudActual", 0);
		actualizarPanel("MetasActual", 0);
		actualizarPanel("ColisionesActual", 0);
	}
	
	/**
	 * Determina si la posicion del ratón en el momento de haber activado un evento de
	 * 'click' se encuentra en la 'hitbox' de alguna entidad y de ser así comienza a 
	 * monitorizar esa entidad
	 * @param posRaton: punto en el que se ha hecho click con el ratón
	 */
	public void seleccionarEntidad(PVector posRaton) {
		Poblacion poblacion = modelo.getPoblacion();
		//Si aún no se ha generado la población, no hace nad
		if(poblacion == null) {
			return;
		}
		/* Recorre todas las entidades desde la última posición (la más superpuesta al haber
		 * sido la última en dibujarse en pantalla). Si alguna contiene en su hitbox la
		 * posición del ratón, sustituye la entidad monitorizada por ella y termina de buscar
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
	 * que está siendo monitorizada actualmente
	 * @param entidad 
	 */
	public void monitorizarEntidad(Entidad entidad) {
		actualizarPanel("Entidad", entidad.getIndice());
		//Redondeamos el valor de la distancia a dos decimales para no mostrar demasiados números
		actualizarPanel("DistanciaEntidad", redondearValor(entidad.getDistancia(), 2));
		actualizarPanel("DistanciaMinEntidad", redondearValor(entidad.getDistanciaMinima(), 2));
		//Para los vectores redondeamos hasta 4 decimales ya que debe mostrar más precisión
		actualizarPanel("Posicion", "("+ redondearValor(entidad.getPosicion().x, 4) +
				", " + redondearValor(entidad.getPosicion().y, 4) + ")");
		actualizarPanel("Velocidad", "("+ redondearValor(entidad.getVelocidad().x, 4) + 
				", " + redondearValor(entidad.getVelocidad().y, 4) + ")");
		actualizarPanel("Aceleracion", "("+ redondearValor(entidad.getAceleracion().x, 4) +
				", " + redondearValor(entidad.getAceleracion().y, 4) + ")");
		actualizarPanel("TiempoEntidad", entidad.getTiempoObtenido()); 
		//La aptitud no la redondeamos porque siempre son números muy pequeños
		actualizarPanel("AptitudEntidad", entidad.getAptitud());
		//Según la entidad tenga determinadas flags o no, mostrará en el estado en el que está
		String estado = entidad.isHaChocado() ? "Chocado" : entidad.isHaLlegado() ? "Llegado" : "Activa";
		actualizarPanel("EstadoEntidad", estado);
	}
	
	/**
	 * Redondea un valor decimal hasta el número de decimales indicado
	 * @param valor
	 * @param numDecimales 
	 * @return el valor redondeado
	 */
	private double redondearValor(float valor, int numDecimales) {
		double factor = Math.pow(10, numDecimales);
		return Math.round(valor * factor) / factor;
	}
	
	/**
	 * Vacía la entidad monitorizada y sus datos del panel de control cuando su generación ya no exista
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
	 * Iniciará la población de entidades a partir de los parámetros introducidos en el 
	 * panel de control para que de comienzo al proceso evolutivo. Una vez iniciado 
	 * cambia el botón de "Empezar" a un botón de "Siguiente" y cambiará su comportamiento. 
	 * @author Alberto
	 */
	public class BtnEmpezarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Actualiza el estado a "realizando ciclo" ya que se empieza a ejecutar el proceso evolutivo
			estado = Estado.REALIZANDO_CICLO;
			//Iniciamos la población con los parámetros iniciales, incluido el punto de spawn del circuito
			modelo.setPoblacionEntidades(setupPoblacion(), modelo.getCircuito().setSpawn());
			/* Actualizamos la flag de parado para que la ventana pueda empezar a llamar a la función de
			 * manipularProceso() cada frame
			 */
			parado = false; 
			/* Cambiamos el texto del botón a "Siguiente", y le cambiamos el action listener por otro para
			 * que se dispare un evento distinto cuando se interactúe con él
			 */
			//Guardamos el tiempo de vida en la cache para que pueda iniciar el proceso
			tiempoVidaCache = modelo.getPoblacion().getTiempoVida();
			PanelControl panelControl = vista.getPanelControl();
			JButton btnProceder = panelControl.getBtnProceder();
			btnProceder.setText("Siguiente");
			btnProceder.addActionListener(new BtnSiguienteListener()); //añadimos el otro listener
			btnProceder.removeActionListener(this); //eliminamos el actual
			//Deshabilitamos el botón hasta que se pueda avanzar de generación
			btnProceder.setEnabled(false); 
			/* Deshabilita el selector de circuitos hasta que se reinicie al estado inicial
			 * ya que cambiar el circuito en mitad del proceso es contraproducente
			 */
			panelControl.getcBoxCircuito().setEnabled(false);
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
	 * Actualiza la flag de parado a false para que pueda realizar el ciclo de vida de las entidades 
	 * @author Alberto
	 */
	public class BtnSiguienteListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Actualiza el estado a "realizando ciclo" ya que se reanuda el proceso evolutivo
			estado = Estado.REALIZANDO_CICLO;
			parado = false;
			//Habilita el botón de pausa ya que ya no provocaría que iniciase otro ciclo
			vista.getPanelControl().getBtnPausar().setEnabled(true);
			//Desactiva el propio botón hasta que se pueda pasar a la siguiente generación
			vista.getPanelControl().getBtnProceder().setEnabled(false);
			limpiarEntidadMonitorizada();
			limpiarUltimaGeneracion();
		}
	}
	
	/**
	 * Actualiza la flag de parado a true para que pause el ciclo de vida de las entidades
	 * hasta reanudar pulsando el mismo botón (actualizando la flag de nuevo)
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
			/* Si se ha pausado la ejecución, actualiza el estado a "pausado", y si se ha
			 * reanudado, se actualiza a "realizando ciclo */
			estado = (parado) ? Estado.PAUSADO : Estado.REALIZANDO_CICLO;
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
			//Actualiza el estado a "en espera" ya que se ha dejado de ejecutar el proceso evolutivo
			estado = Estado.EN_ESPERA;
			//Impide que la ventana siga actualizando hasta que volvamos a empezar
			parado = true; 
			Ventana ventana = vista.getVentana();
			/* Hay que reiniciar el número de frames porque si no se produce un bug
			 * en el que tras empezar de nuevo, empieza a contar desde el frame en el
			 * que estaba al pausar el proceso antes de reiniciar, rompiendo el programa
			 */
			ventana.setNumFramesGen(0); 
			/* Dejamos activado sólo el botón de empezar y el selector de circuitos, que
			 * ahora no tendría problema para poder cargar otro distinto al haber empezado
			 * otro proceso
			 */
			PanelControl panelControl = vista.getPanelControl();
			panelControl.getcBoxCircuito().setEnabled(true);
			panelControl.getBtnReiniciar().setEnabled(false);
			panelControl.getBtnPausar().setEnabled(false);
			panelControl.getBtnPausar().setText("Pausar");
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
			 * modificarlo si está en modo "Empezar", ya que solo debe comenzar una población
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
	
	/**
	 * Establece un nuevo valor para el parámetro de la población de entidades, a través 
	 * del cambio detectado en el spinner. Realiza las restricciones necesarias antes
	 * de modificar el parámetro deseado. Sirve para cualquiera de los spinners
	 * @author Alberto
	 */
	public class SpParamListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSpinner spinner = (JSpinner) e.getSource();
			//Obtiene el nombre del parámetro a cambiar a partir del nombre del spinner
			String param = spinner.getName(); 
			int valor = (int) spinner.getValue(); //valor cambiado en el spinner
			/* Si el spinner es el correspondiente al tiempo de vida, debe controlar que
			 * su valor nunca sea menor o igual que el tiempo objetivo, así que en ese caso
			 * altera el valor para que esté por encima
			 */
			if(param.equals("TiempoVida")) {
				int valorSpObjetivo = (int)vista.getPanelControl().getSpTiempoObjetivo().getValue();
				if(valor <= valorSpObjetivo) {
					spinner.setValue(valorSpObjetivo + 10);
					valor = (int)spinner.getValue();
				}
			}
			/* Al principio del programa la población no está inicializada así que no debe actualizar
			 * ningún parámetro en ese caso, ya se encarga el botón "Empezar" de capturar los valores
			 */
			if(modelo.getPoblacion() != null) {
				//Si población ya está inicializada puede actualizarla sin problema
				actualizarParam(param, valor);
			}
		}

		/**
		 * Según el nombre del parámetro actualiza el correspondiente de la población
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
	 * Cambia el circuito cargando el fichero correspondiente a la opción que se ha 
	 * seleccionado en el combo box y reestableciendo los parámetros necesarios en los
	 * elementos del proceso como la meta, obstáculos y la población a partir del circuito.
	 * En el siguiente frame se actualizara el circuito mostrado en la ventana gráfica.
	 * @author Alberto
	 */
	public class CboxCircuitoListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			//Solo se activa si se selecciona una opción distinta a la actual
			if(e.getStateChange() == ItemEvent.SELECTED) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cBoxCircuito = (JComboBox<String>) e.getSource();
				//Obtiene el nombre del circuito a partir del item seleccionado
				String circuitoSeleccionado = (String) cBoxCircuito.getSelectedItem();
				/* Inicia los elementos (meta y obstáculos) del circuito tras cargar el ficheros
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

