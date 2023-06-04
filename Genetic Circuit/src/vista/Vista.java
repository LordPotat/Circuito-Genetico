package vista;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controlador.Controlador;

/**
 * Contiene todas las ventanas de interfaz de usuario para visualizar y manejar
 * el programa
 * @author Alberto
 */
public class Vista {
	
	/**
	 * La ventana gr�fica de Processing donde se dibujan todos los elementos del
	 * del circuito y las entidades */
	private Ventana ventana;
	/**
	 * La ventana de Swing (JFrame) que proporciona controles de usuario para manipular
	 * la ejecuci�n el programa y el proceso evolutivo, as� como mostrar los datos
	 * relativos a �ste */
	private PanelControl panelControl;
	
	/**
	 * Crea y muestra ambas ventanas de la vista pas�ndoles el controlador del programa
	 * para que puedan interaccionar con otros componentes a trav�s de �l
	 * @param controlador */
	public Vista(Controlador controlador) {
		//Inicia el estilo visual del JFrame
		initLookAndFeel();
		/* Crea la ventana de Processing llamando al m�todo de "f�brica", ya que solo
		 * existir� una �nica instancia en la ejecuci�n del programa */
		ventana = Ventana.crearVentana(controlador);
		String[] processingArgs = {"Circuito Gen�tico"}; //T�tulo mostrado en la ventana
		/* Delega su ejecuci�n a un hilo paralelo, ya que no puede ejecutar el JFrame
		 * y el sketch de Processing a la vez en el hilo principal */
	    Thread hiloProcessing = new Thread(() -> {
	    	/* Para ejecutar y renderizar el sketch debe recibir unos argumentos y la 
	    	 * instancia de la ventana */
	    	Ventana.runSketch(processingArgs, ventana);
	    });
	    hiloProcessing.start(); 
		//Inicia el JFrame del panel de control
		panelControl = new PanelControl(controlador); 	
	}

	public Ventana getVentana() {
		return ventana;
	}

	public PanelControl getPanelControl() {
		return panelControl;
	}
	
	/**
	 * Establece el "LookAndFeel", que es el estilo visual que se muestra en la interfaz
	 * de Swing. En este caso se muestra el estilo "Nimbus"
	 */
	private void initLookAndFeel() {
		try {
			//Recorre todos los LookAndFeels instalados y si aparece Nimbus, le aplica ese estilo a la UI
	        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	            if ("Nimbus".equals(info.getName())) {
	                UIManager.setLookAndFeel(info.getClassName());
	                break;
	            }
	        }
	    } catch (ClassNotFoundException ex) {
	        Logger.getLogger(Vista.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (InstantiationException ex) {
	        Logger.getLogger(Vista.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (IllegalAccessException ex) {
	        Logger.getLogger(Vista.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (UnsupportedLookAndFeelException ex) {
	        Logger.getLogger(Vista.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
}
