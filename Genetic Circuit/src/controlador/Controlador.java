package controlador;

import java.util.HashMap;
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
		HashMap<String, Object> metaParams = new HashMap<String, Object>();
		metaParams.put("Posicion", new PVector(vista.getVentana().width - 10, 10));
		metaParams.put("Ancho", 10f);	
		metaParams.put("Alto", 10f);		
		modelo = new Modelo(metaParams, 8, null)  ;
	}
	
	public void iniciar() {
		
	}
	
	
}

