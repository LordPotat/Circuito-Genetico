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
 * con algún control del panel de control o pulsando teclas en la ventana gráfica
 * @author Alberto
 */
public class ControladorEventos {
	
	private Modelo modelo;
	private Vista vista;
	private Controlador controlador;
	
	/**
	 * Crea el controlador de eventos dándole acceso al controlador principal, el modelo y la vista
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
	 * Determina qué evento debería desencadenarse cuando se pulse la tecla "espacio"
	 * según las circunstancias actuales del programa
	 */
	public void realizarEventoEspacio() {
		/* Si el botón de proceder corresponde a "Empezar", indica que está en el estado
		 * inicial y ejecuta empezar(). */
		if(vista.getPanelControl().getBtnProceder().getText().equals("Empezar")) {
			empezar();
		} 
		/* De lo contrario, implica que el proceso ya ha comenzado. Si en ese momento no está
		 * en el estado "EN_ESPERA" quiere decir que la generacion todavía no ha acabado
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
	 * Iniciará la población de entidades a partir de los parámetros introducidos en el 
	 * panel de control para que de comienzo al proceso evolutivo. Una vez iniciado 
	 * cambia el botón de "Empezar" a un botón de "Siguiente" y cambiará su comportamiento. 
	 */
	public void empezar() {
		//Actualiza el estado a "realizando ciclo" ya que se empieza a ejecutar el proceso evolutivo
		controlador.setEstado(Estado.REALIZANDO_CICLO);
		//Iniciamos la población con los parámetros iniciales, incluido el punto de spawn del circuito
		modelo.setPoblacionEntidades(setupPoblacion(), modelo.getCircuito().setSpawn());
		/* Actualizamos la flag de parado para que la ventana pueda empezar a llamar a la función de
		 * manipularProceso() cada frame
		 */
		controlador.setParado(false); 
		/* Cambiamos el texto del botón a "Siguiente", y le cambiamos el action listener por otro para
		 * que se dispare un evento distinto cuando se interactúe con él
		 */
		//Guardamos el tiempo de vida en la cache para que pueda iniciar el proceso
		controlador.setTiempoVidaCache(modelo.getPoblacion().getTiempoVida());
		PanelControl panelControl = vista.getPanelControl();
		JButton btnProceder = panelControl.getBtnProceder();
		btnProceder.setText("Siguiente");
		btnProceder.addActionListener((e) -> continuar()); //añadimos el otro listener
		btnProceder.removeActionListener(btnProceder.getActionListeners()[0]); //eliminamos el actual
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
	 * Actualiza la flag de parado a true para que pause el ciclo de vida de las entidades
	 * hasta reanudar pulsando el mismo botón (actualizando la flag de nuevo)
	 */
	public void pausar() {
		controlador.setParado(!controlador.isParado()); //Invierte el estado de la flag
		PanelControl panelControl = vista.getPanelControl();
		//Actualiza el texto a "Reanudar" o "pausar" según está parado o no
		String textoBtn = (controlador.isParado()) ? "Reanudar" : "Pausar";
		panelControl.getBtnPausar().setText(textoBtn);
		/* Si se ha pausado la ejecución, actualiza el estado a "pausado", y si se ha
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
		//Habilita el botón de pausa ya que ya no provocaría que iniciase otro ciclo
		vista.getPanelControl().getBtnPausar().setEnabled(true);
		//Desactiva el propio botón hasta que se pueda pasar a la siguiente generación
		vista.getPanelControl().getBtnProceder().setEnabled(false);
		controlador.limpiarEntidadMonitorizada();
		controlador.limpiarUltimaGeneracion();
	}
	
	/**
	 * Para el actual proceso evolutivo y restaura el panel de control al estado inicial
	 * del programa para poder iniciar de nuevo otra ejecución de cero
	 */
	public void reiniciar() {
		//Actualiza el estado a "en espera" ya que se ha dejado de ejecutar el proceso evolutivo
		controlador.setEstado(Estado.EN_ESPERA);
		//Impide que la ventana siga actualizando hasta que volvamos a empezar
		controlador.setParado(true); 
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
		/* Cambiamos el botón de "Siguiente" por "Empezar" y le asignamos un nuevo listener
		 * que llame a empezar(), que es el evento que le corresponde ahora
		 */
		btnProceder.setText("Empezar");
		btnProceder.removeActionListener(btnProceder.getActionListeners()[0]);
		btnProceder.addActionListener((e) -> empezar());
		//Reseteamos la información mostrada en el panel de control
		reiniciarInfoPanel(panelControl);
	}
	
	/**
	 * Activa o desactiva según se marque la checkbox el modo automático. Si se activa
	 * las generaciones se sucederán sólas sin necesidad del input del usuario para
	 * continuar su ciclo de vida. Si se desactiva el usuario deberá pulsar el botón
	 * de "Siguiente" para avanzar de generación.
	 * @e evento que se desencadena cuando se marca o desmarca un JCheckBox
	 */
	public void cambiarModo(ActionEvent e) {
		//Obtenemos si el checkbox que lanza el evento está o no activado
		JCheckBox cbModoAuto = (JCheckBox) e.getSource();
		boolean activado = cbModoAuto.isSelected();
		//Activa o desactiva el modo automático según esté seleccionado
		controlador.setModoAutomatico(activado);
		PanelControl panelControl = vista.getPanelControl();
		JButton btnProceder = panelControl.getBtnProceder();
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
				controlador.setParado(false);
				/* Deshabilita el botón de "Siguiente" solo si el proceso estaba parado,
				 * para que en el siguiente ciclo ya no permita avanzar manualmente
				 */
				btnProceder.setEnabled(!activado);
				//Habilita el botón de pausa para que pueda volver a pausar el proceso
				panelControl.getBtnPausar().setEnabled(true);
			} 
		}
	}
	
	/**
	 * Simula un click en el check box del modo automático para activar o desactivarlo
	 */
	public void cambiarModo() {
		vista.getPanelControl().getCbModoAutomatico().doClick();
	}
	
	/**
	 * Cambia el circuito cargando el fichero correspondiente a la opción que se ha 
	 * seleccionado en el combo box y reestableciendo los parámetros necesarios en los
	 * elementos del proceso como la meta, obstáculos y la población a partir del circuito.
	 * En el siguiente frame se actualizara el circuito mostrado en la ventana gráfica.
	 * @e el evento que se desencadena cuando se selecciona una opción de un JComboBox
	 */
	public void elegirCircuito(ItemEvent e) {
		//Solo se activa si se selecciona una opción distinta a la actual
		if(e.getStateChange() == ItemEvent.SELECTED) {
			@SuppressWarnings("unchecked")
			JComboBox<String> cBoxCircuito = (JComboBox<String>) e.getSource();
			//Obtiene el nombre del circuito a partir del item seleccionado
			String circuitoSeleccionado = (String) cBoxCircuito.getSelectedItem();
			/* Inicia los elementos (meta y obstáculos) del circuito tras cargar el ficheros
			 * con el nombre del circuito seleccionado
			 */
			controlador.iniciarCircuito(circuitoSeleccionado, vista.getVentana());
		}
		
	}
	
	/**
	 * Establece un nuevo valor para el parámetro de la población de entidades, a través 
	 * del cambio detectado en el spinner. Realiza las restricciones necesarias antes
	 * de modificar el parámetro deseado. Sirve para cualquiera de los spinners
	 * @e el evento que se desencadena cuando se cambia el valor de un JSpinner
	 */
	public void modificarParametro(ChangeEvent e) {
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
	 * Cierra el programa sin importar el estado en el que se encuentre
	 */
	public void salir() {
		vista.getVentana().exit();
		System.exit(0);
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
		controlador.limpiarEntidadMonitorizada();
		controlador.limpiarUltimaGeneracion();
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
