package controlador;

import java.util.HashMap;
import modelo.Modelo;
import modelo.entidades.Entidad;
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
		modelo = new Modelo(this);
		modelo.setMeta(modelo.getCircuito().setupMeta(ventana));
		modelo.setObstaculos(modelo.getCircuito().setupObstaculos(ventana));
		modelo.setPoblacionEntidades(setupPoblacion(), modelo.getCircuito().setSpawn(ventana));
	}

	private HashMap<String, Integer> setupPoblacion() {
		HashMap<String, Integer> poblacionParams = new HashMap<String, Integer>();
		poblacionParams.put("NumEntidades", 300);
		poblacionParams.put("TasaMutacion", 20);	
		poblacionParams.put("TiempoVida", 400);
		return poblacionParams;
	}
	
	public void iniciar() {
		
	}

	public void mostrarRutaOptima(Entidad mejorEntidad) {
		vista.getVentana().drawRutaOptima(mejorEntidad.getAdn().getGenes(), mejorEntidad.getTiempoObtenido());
		vista.getVentana().drawEntidad(mejorEntidad.getPosicion(), mejorEntidad.getVelocidad());
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

