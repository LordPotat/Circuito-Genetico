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
 * @author Alberto P�rez
 */
public class Controlador {
	
	private Vista vista;
	private Modelo modelo;
	
	/**
	 * Flag que indica si el proceso evolutivo debe comenzar
	 */
	private boolean iniciado = false;
	/**
	 * Flag que indica si el proceso evolutivo est� parado hasta que se reactive
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
	 * Inicia los objetos del modelo de datos: la meta, obstaculos y poblaci�n
	 * Obtiene los datos almacenados en el circuito escogido para configurar sus par�metros
	 */
	public void iniciar() {
		Ventana ventana = vista.getVentana();
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
			panelControl.setValor("Generacion", entidades.getNumGeneraciones());
			mostrarRutaOptima(entidades.getMejorEntidad());
			return;
		}
		//N�mero de frames que han pasado desde el inicio de su ciclo de vida
		int numFramesGen = ventana.getNumFramesGen();
		//Si todav�a le queda tiempo de vida a la poblaci�n, realiza un ciclo de ejecuci�n
		if(numFramesGen < entidades.getTiempoVida()) {
			entidades.realizarCiclo();
			ventana.setNumFramesGen(++numFramesGen);
		} 
		//De lo contrario, la poblaci�n debe evolucionar y reiniciar su ciclo de vida
		else {
			ventana.setNumFramesGen(0);
			entidades.evolucionar();
			//Desabilita el bot�n de pausa para que no pueda iniciar otra generaci�n por error
			vista.getPanelControl().getBtnPausar().setEnabled(false);
			//Para el ciclo hasta que se reanude pulsando el bot�n o con el modo autom�tico
			parado = true; 
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
		vista.getVentana().drawEntidad(mejorEntidad.getPosicion(), mejorEntidad.getVelocidad());
	}
	
	/**
	 * Muestra una entidad en la ventana gr�fica en la posici�n y direcci�n actual
	 * @param entidad que se debe mostrar
	 */
	public void mostrarEntidad(Entidad entidad) {
		vista.getVentana().drawEntidad(entidad.getPosicion(), entidad.getVelocidad());
	}
	
	public <T> void actualizarPanel(String label, T valor) {
		vista.getPanelControl().setValor(label, valor);
	}
	
	/**
	 * Implementa el evento desencadenado al pulsar el bot�n de empezar. Se actualizar� el estado a
	 * iniciado y se iniciar� la poblaci�n de entidades a partir de los par�metros introducidos en el
	 * panel de control para que de comienzo al proceso evolutivo. Una vez iniciado cambia el bot�n 
	 * de "Empezar" a un bot�n de "Siguiente" y cambiar� su comportamiento. 
	 * @author Alberto
	 */
	public class BtnEmpezarListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			iniciado = true;
			//Iniciamos la poblaci�n con los par�metros iniciales, incluido el punto de spawn del circuito
			modelo.setPoblacionEntidades(setupPoblacion(), modelo.getCircuito().setSpawn(vista.getVentana()));
			/* Actualizamos la flag de parado para que la ventana pueda empezar a llamar a la funci�n de
			 * manipularProceso() cada frame
			 */
			parado = false; 
			/* Cambiamos el texto del bot�n a "Siguiente", y le cambiamos el action listener por otro para
			 * que se dispare un evento distinto cuando se interact�e con �l
			 */
			PanelControl panelControl = vista.getPanelControl();
			JButton btnProceder = panelControl.getBtnProceder();
			btnProceder.setText("Siguiente");
			btnProceder.addActionListener(new BtnSiguienteListener()); //a�adimos el otro listener
			btnProceder.removeActionListener(this); //eliminamos el actual
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
	 * Implementa el evento desencadenado por el bot�n "Siguiente". Actualiza la flag
	 * de parado a false para que pueda realizar el ciclo de vida de las entidades
	 * @author Alberto
	 */
	public class BtnSiguienteListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parado = false;
			//Habilita el bot�n de pausa ya que ya no provocar�a que iniciase otro ciclo
			vista.getPanelControl().getBtnPausar().setEnabled(true);
		}
	}
	
	/**
	 * Implementa el evento desencadenado por el bot�n "Pausar". Actualiza la flag
	 * de parado a true para que pause el ciclo de vida de las entidades hasta reanudar
	 * pulsando el mismo bot�n (actualizando la flag de nuevo)
	 * @author Alberto
	 */
	public class BtnPausarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parado = !parado; //Invierte el estado de la flag
			PanelControl panelControl = vista.getPanelControl();
			//Deshabilita el bot�n de "Siguiente" si est� parado y lo habilita si no lo est�
			panelControl.getBtnProceder().setEnabled(!parado);
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

