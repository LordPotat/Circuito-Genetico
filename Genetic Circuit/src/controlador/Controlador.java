package controlador;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import modelo.Modelo;
import processing.core.PVector;
import vista.Vista;

public class Controlador {
	
	private Vista vista;
	private Modelo modelo;
	
	public Controlador() {
		vista = new Vista();
		initModelo();
	}

	private void initModelo() {
		HashMap<String, ?> metaParams = (HashMap<String, ?>) Map.of(
				"Posicion", new PVector(vista.getVentana().width - 10, 10),
				"Ancho", 10f,
				"Alto", 10f
		);
		modelo = new Modelo(metaParams, 8, null)  ;
	}
	
	public void iniciar() {
		
	}
	
	
}

