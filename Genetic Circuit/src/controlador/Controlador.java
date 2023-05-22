package controlador;

import java.util.HashMap;
import modelo.Modelo;
import processing.core.PVector;
import vista.Vista;
import vista.ventana_grafica.Ventana;

public class Controlador {
	
	private Vista vista;
	private Modelo modelo;
	
	public Controlador() {
		vista = new Vista(this);
		initModelo();
	}

	private void initModelo() {
		Ventana ventana = vista.getVentana();
		HashMap<String, Object> metaParams = setupMeta(ventana);		
		modelo = new Modelo(metaParams, 8, null);
		HashMap<String, Integer> poblacionParams = setupPoblacion();	
		modelo.setPoblacionEntidades(poblacionParams, new PVector(10, ventana.height-10));
	}

	private HashMap<String, Integer> setupPoblacion() {
		HashMap<String, Integer> poblacionParams = new HashMap<String, Integer>();
		poblacionParams.put("NumEntidades", 20);
		poblacionParams.put("TasaMutacion", 1);	
		poblacionParams.put("TiempoVida", 10);
		return poblacionParams;
	}

	private HashMap<String, Object> setupMeta(Ventana ventana) {
		HashMap<String, Object> metaParams = new HashMap<String, Object>();
		metaParams.put("Posicion", new PVector(ventana.width - 10, 10));
		metaParams.put("Ancho", 50f);	
		metaParams.put("Alto", 50f);
		return metaParams;
	}
	
	public void iniciar() {
		
	}
	
	
}

