package controlador;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import modelo.Modelo;
import vista.Vista;

public class Controlador {
	
	private Vista vista;
	private Modelo modelo;
	
	public Controlador() {
		initLookAndFeel();
		vista = new Vista();
		modelo = new Modelo();
		System.out.println("Holiii");
	}
	
	public void iniciar() {
		
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

