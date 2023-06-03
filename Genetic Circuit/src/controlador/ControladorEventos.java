package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;

import modelo.Modelo;
import vista.Vista;
import vista.panel_control.PanelControl;
import vista.ventana_grafica.Ventana;

/**
 * Se encarga de gestionar todo lo relativo a los eventos que se ejecutan cuando se interacciona
 * con alg�n control del panel de control o pulsando teclas en la ventana gr�fica
 * @author Alberto
 */
public class ControladorEventos {
	
	private Modelo modelo;
	private Vista vista;
	private Controlador controlador;
	
	/**
	 * Crea el controlador de eventos d�ndole acceso al controlador principal, el modelo y la vista
	 * @param controlador
	 * @param modelo
	 * @param vista
	 */
	public ControladorEventos(Controlador controlador, Modelo modelo, Vista vista) {
		this.controlador = controlador;
		this.modelo = modelo;
		this.vista = vista;
	}
	
	/** 
	 * Determina qu� evento deber�a desencadenarse cuando se pulse la tecla "espacio"
	 * seg�n las circunstancias actuales del programa
	 */
	public void realizarEventoEspacio() {
		/* Si el bot�n de proceder corresponde a "Empezar", indica que est� en el estado
		 * inicial y ejecuta empezar(). */
		if(vista.getPanelControl().getBtnProceder().getText().equals("Empezar")) {
			empezar();
		} 
		/* De lo contrario, implica que el proceso ya ha comenzado. Si en ese momento no est�
		 * en el estado "EN_ESPERA" quiere decir que la generacion todav�a no ha acabado
		 * su ciclo de vida y por tanto ejecuta pausar()
		 */
		else if(controlador.getEstado() != Estado.EN_ESPERA){
			pausar();
		} 
		//Para el resto de casos restantes, debe ejecutar continuar()
		else {
			continuar();
		}
	}
	
	/**
	 * Iniciar� la poblaci�n de entidades a partir de los par�metros introducidos en el 
	 * panel de control para que de comienzo al proceso evolutivo. Una vez iniciado 
	 * cambia el bot�n de "Empezar" a un bot�n de "Siguiente" y cambiar� su comportamiento. 
	 */
	public void empezar() {
		//Actualiza el estado a "realizando ciclo" ya que se empieza a ejecutar el proceso evolutivo
		controlador.setEstado(Estado.REALIZANDO_CICLO);
		//Iniciamos la poblaci�n con los par�metros iniciales, incluido el punto de spawn del circuito
		modelo.setPoblacionEntidades(setupPoblacion(), modelo.getCircuito().setSpawn());
		/* Actualizamos la flag de parado para que la ventana pueda empezar a llamar a la funci�n de
		 * manipularProceso() cada frame
		 */
		controlador.setParado(false); 
		/* Cambiamos el texto del bot�n a "Siguiente", y le cambiamos el action listener por otro para
		 * que se dispare un evento distinto cuando se interact�e con �l
		 */
		//Guardamos el tiempo de vida en la cache para que pueda iniciar el proceso
		controlador.setTiempoVidaCache(modelo.getPoblacion().getTiempoVida());
		PanelControl panelControl = vista.getPanelControl();
		JButton btnProceder = panelControl.getBtnProceder();
		btnProceder.setText("Siguiente");
		btnProceder.addActionListener((e) -> continuar()); //a�adimos el otro listener
		btnProceder.removeActionListener(btnProceder.getActionListeners()[0]); //eliminamos el actual
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
	 * Actualiza la flag de parado a true para que pause el ciclo de vida de las entidades
	 * hasta reanudar pulsando el mismo bot�n (actualizando la flag de nuevo)
	 */
	public void pausar() {
		controlador.setParado(!controlador.isParado()); //Invierte el estado de la flag
		PanelControl panelControl = vista.getPanelControl();
		//Actualiza el texto a "Reanudar" o "pausar" seg�n est� parado o no
		String textoBtn = (controlador.isParado()) ? "Reanudar" : "Pausar";
		panelControl.getBtnPausar().setText(textoBtn);
		/* Si se ha pausado la ejecuci�n, actualiza el estado a "pausado", y si se ha
		 * reanudado, se actualiza a "realizando ciclo */
		controlador.setEstado((controlador.isParado()) ? Estado.PAUSADO : Estado.REALIZANDO_CICLO);
	}
	
	/**
	 * Actualiza la flag de parado a false para que pueda realizar el ciclo de vida de las entidades 
	 */
	public void continuar() {
		//Actualiza el estado a "realizando ciclo" ya que se reanuda el proceso evolutivo
		controlador.setEstado(Estado.REALIZANDO_CICLO);
		controlador.setParado(false);
		//Habilita el bot�n de pausa ya que ya no provocar�a que iniciase otro ciclo
		vista.getPanelControl().getBtnPausar().setEnabled(true);
		//Desactiva el propio bot�n hasta que se pueda pasar a la siguiente generaci�n
		vista.getPanelControl().getBtnProceder().setEnabled(false);
		controlador.limpiarEntidadMonitorizada();
		controlador.limpiarUltimaGeneracion();
	}
	
	/**
	 * Para el actual proceso evolutivo y restaura el panel de control al estado inicial
	 * del programa para poder iniciar de nuevo otra ejecuci�n de cero
	 */
	public void reiniciar() {
		//Actualiza el estado a "en espera" ya que se ha dejado de ejecutar el proceso evolutivo
		controlador.setEstado(Estado.EN_ESPERA);
		//Impide que la ventana siga actualizando hasta que volvamos a empezar
		controlador.setParado(true); 
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
		/* Cambiamos el bot�n de "Siguiente" por "Empezar" y le asignamos un nuevo listener
		 * que llame a empezar(), que es el evento que le corresponde ahora
		 */
		btnProceder.setText("Empezar");
		btnProceder.removeActionListener(btnProceder.getActionListeners()[0]);
		btnProceder.addActionListener((e) -> empezar());
		//Reseteamos la informaci�n mostrada en el panel de control
		reiniciarInfoPanel(panelControl);
	}
	
	/**
	 * Activa o desactiva seg�n se marque la checkbox el modo autom�tico. Si se activa
	 * las generaciones se suceder�n s�las sin necesidad del input del usuario para
	 * continuar su ciclo de vida. Si se desactiva el usuario deber� pulsar el bot�n
	 * de "Siguiente" para avanzar de generaci�n.
	 * @e evento que se desencadena cuando se marca o desmarca un JCheckBox
	 */
	public void cambiarModo(ActionEvent e) {
		//Obtenemos si el checkbox que lanza el evento est� o no activado
		JCheckBox cbModoAuto = (JCheckBox) e.getSource();
		boolean activado = cbModoAuto.isSelected();
		//Activa o desactiva el modo autom�tico seg�n est� seleccionado
		controlador.setModoAutomatico(activado);
		PanelControl panelControl = vista.getPanelControl();
		JButton btnProceder = panelControl.getBtnProceder();
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
				controlador.setParado(false);
				/* Deshabilita el bot�n de "Siguiente" solo si el proceso estaba parado,
				 * para que en el siguiente ciclo ya no permita avanzar manualmente
				 */
				btnProceder.setEnabled(!activado);
				//Habilita el bot�n de pausa para que pueda volver a pausar el proceso
				panelControl.getBtnPausar().setEnabled(true);
			} 
		}
	}
	
	/**
	 * Simula un click en el check box del modo autom�tico para activar o desactivarlo
	 */
	public void cambiarModo() {
		vista.getPanelControl().getCbModoAutomatico().doClick();
	}
	
	/**
	 * Cambia el circuito cargando el fichero correspondiente a la opci�n que se ha 
	 * seleccionado en el combo box y reestableciendo los par�metros necesarios en los
	 * elementos del proceso como la meta, obst�culos y la poblaci�n a partir del circuito.
	 * En el siguiente frame se actualizara el circuito mostrado en la ventana gr�fica.
	 * @e el evento que se desencadena cuando se selecciona una opci�n de un JComboBox
	 */
	public void elegirCircuito(ItemEvent e) {
		//Solo se activa si se selecciona una opci�n distinta a la actual
		if(e.getStateChange() == ItemEvent.SELECTED) {
			@SuppressWarnings("unchecked")
			JComboBox<String> cBoxCircuito = (JComboBox<String>) e.getSource();
			//Obtiene el nombre del circuito a partir del item seleccionado
			String circuitoSeleccionado = (String) cBoxCircuito.getSelectedItem();
			/* Inicia los elementos (meta y obst�culos) del circuito tras cargar el ficheros
			 * con el nombre del circuito seleccionado
			 */
			controlador.iniciarCircuito(circuitoSeleccionado, vista.getVentana());
		}
		
	}
	
	/**
	 * Establece un nuevo valor para el par�metro de la poblaci�n de entidades, a trav�s 
	 * del cambio detectado en el spinner. Realiza las restricciones necesarias antes
	 * de modificar el par�metro deseado. Sirve para cualquiera de los spinners
	 * @e el evento que se desencadena cuando se cambia el valor de un JSpinner
	 */
	public void modificarParametro(ChangeEvent e) {
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
	 * Cierra el programa sin importar el estado en el que se encuentre
	 */
	public void salir() {
		vista.getVentana().exit();
		System.exit(0);
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
		controlador.limpiarEntidadMonitorizada();
		controlador.limpiarUltimaGeneracion();
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
