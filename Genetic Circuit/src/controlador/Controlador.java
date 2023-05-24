package controlador;

import java.util.HashMap;
import modelo.Modelo;
import modelo.circuitos.CircuitoEjemplo;
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
		modelo.setMeta(CircuitoEjemplo.setupMeta(ventana));
		modelo.setObstaculos(CircuitoEjemplo.setupObstaculos(ventana));
		modelo.setPoblacionEntidades(setupPoblacion(), CircuitoEjemplo.setSpawn(ventana));
	}

	private HashMap<String, Integer> setupPoblacion() {
		HashMap<String, Integer> poblacionParams = new HashMap<String, Integer>();
		poblacionParams.put("NumEntidades", 150);
		poblacionParams.put("TasaMutacion", 3);	
		poblacionParams.put("TiempoVida", 400);
		return poblacionParams;
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

