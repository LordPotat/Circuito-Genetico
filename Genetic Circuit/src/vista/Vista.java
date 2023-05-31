package vista;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controlador.Controlador;
import vista.panel_control.PanelControl;
import vista.ventana_grafica.Ventana;

public class Vista {
	
	private Ventana ventana;
	private PanelControl panelControl;
	
	public Vista(Controlador controlador) {
		initLookAndFeel();
		
		String[] processingArgs = {"Circuito Genético"};
		ventana = Ventana.crearVentana(controlador);
	    Thread hiloProcessing = new Thread(() -> {
	    	Ventana.runSketch(processingArgs, ventana);
	    });
	    hiloProcessing.start();
		
		SwingUtilities.invokeLater(() -> {
			panelControl = new PanelControl(controlador);
	    });
		
	}

	public Ventana getVentana() {
		return ventana;
	}

	public PanelControl getPanelControl() {
		return panelControl;
	}
	
	private void initLookAndFeel() {
		try {
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
