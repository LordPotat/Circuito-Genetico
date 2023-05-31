package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;

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
	 * Flag que indica si el proceso evolutivo debe comenzar
	 */
	private boolean iniciado = false;
	/**
	 * Flag que indica si el proceso evolutivo está parado hasta que se reactive
	 */
	private boolean parado = true;
	
	/** 
	 * Inicia el modelo de datos y la vista con la propia instancia para que accedan al controlador
	 * @throws InterruptedException 
	 */
	public Controlador() throws InterruptedException {
		vista = new Vista(this);
		Thread.sleep(250); //Para que de tiempo de cargar la ventana antes de iniciar los datos
		modelo = new Modelo(this);
	}

	/** 
	 * Inicia los objetos del modelo de datos: la meta, obstaculos y población
	 * Obtiene los datos almacenados en el circuito escogido para configurar sus parámetros
	 */
	public void iniciar() {
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
			//Desabilita el botón de pausa para que no pueda iniciar otra generación por error
			vista.getPanelControl().getBtnPausar().setEnabled(false);
			//Para el ciclo hasta que se reanude pulsando el botón o con el modo automático
			parado = true; 
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
	 * Implementa el evento desencadenado al pulsar el botón de empezar. Se actualizará el estado a
	 * iniciado y se iniciará la población de entidades a partir de los parámetros introducidos en el
	 * panel de control para que de comienzo al proceso evolutivo. Una vez iniciado cambia el botón 
	 * de "Empezar" a un botón de "Siguiente" y cambiará su comportamiento. 
	 * @author Alberto
	 */
	public class BtnEmpezarListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			iniciado = true;
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
			//Deshabilita el botón de "Siguiente" si está parado y lo habilita si no lo está
			panelControl.getBtnProceder().setEnabled(!parado);
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
	
	//TODO 
	public class BtnReiniciarListener implements ActionListener {
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			PanelControl panelControl = vista.getPanelControl();
			
		}
	}
	
	//TODO
	public class CbModoAutoListener implements ActionListener {
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public Vista getVista() {
		return vista;
	}

	public Modelo getModelo() {
		return modelo;
	}

	public boolean isIniciado() {
		return iniciado;
	}

	public boolean isParado() {
		return parado;
	}

}

