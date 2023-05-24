package controlador;

import java.util.ArrayList;
import java.util.HashMap;
import modelo.Modelo;
import modelo.entidades.Entidad;
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
		modelo = new Modelo(this, setupMeta(ventana), setupObstaculos(ventana));
		modelo.setPoblacionEntidades(setupPoblacion(), new PVector(ventana.width/2, ventana.height-10));
	}

	private ArrayList<HashMap<String, Object>> setupObstaculos(Ventana ventana) {
		ArrayList<HashMap<String, Object>> obsParams = new ArrayList<HashMap<String, Object>>(); 
		HashMap<String, Object>[] colocacionObs = crearCircuito();
		int numObstaculos = colocacionObs.length;
		for(int i=0; i < numObstaculos; i++) {
			
		}
		return null;
	}

	private HashMap<String, Object>[] crearCircuito() {
		@SuppressWarnings("unchecked")
		HashMap<String, Object>[] colocacionObs = new HashMap[10];
		for(int i=0; i < colocacionObs.length; i++) {
			
			
		}
		return colocacionObs;
	}

	private HashMap<String, Integer> setupPoblacion() {
		HashMap<String, Integer> poblacionParams = new HashMap<String, Integer>();
		poblacionParams.put("NumEntidades", 50);
		poblacionParams.put("TasaMutacion", 1);	
		poblacionParams.put("TiempoVida", 800);
		return poblacionParams;
	}

	private HashMap<String, Object> setupMeta(Ventana ventana) {
		HashMap<String, Object> metaParams = new HashMap<String, Object>();
		metaParams.put("Posicion", new PVector(ventana.width/2, 30));
		metaParams.put("Ancho", 50f);	
		metaParams.put("Alto", 50f);
		return metaParams;
	}
	
	public void iniciar() {
		
	}

	public void mostrarEntidad(Entidad entidad) {
		vista.getVentana().drawEntidad(entidad.getPosicion(), entidad.getVelocidad());
	}
	
	public Vista getVista() {
		return vista;
	}

	public Modelo getModelo() {
		return modelo;
	}
	
}

