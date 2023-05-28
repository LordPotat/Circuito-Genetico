package controlador;

import java.util.HashMap;
import modelo.Modelo;
import modelo.entidades.Entidad;
import modelo.entidades.Poblacion;
import vista.Vista;
import vista.ventana_grafica.Ventana;

public class Controlador {
	
	private Vista vista;
	private Modelo modelo;
	
	public Controlador() {
		vista = new Vista(this);
		modelo = new Modelo(this);
	}

	public void iniciar() {
		Ventana ventana = vista.getVentana();
		modelo.setMeta(modelo.getCircuito().setupMeta(ventana));
		modelo.setObstaculos(modelo.getCircuito().setupObstaculos(ventana));
		modelo.setPoblacionEntidades(setupPoblacion(), modelo.getCircuito().setSpawn(ventana));
	}

	private HashMap<String, Integer> setupPoblacion() {
		HashMap<String, Integer> poblacionParams = new HashMap<String, Integer>();
		poblacionParams.put("NumEntidades", 1500);
		poblacionParams.put("TasaMutacion", 20);	
		poblacionParams.put("TiempoVida", 400);
		return poblacionParams;
	}

	public int manipularPoblacion() {

		Poblacion entidades = modelo.getPoblacion();
		Ventana ventana = vista.getVentana();
		if(entidades.isObjetivoCumplido()) {
			mostrarRutaOptima(entidades.getMejorEntidad());
			return entidades.getNumGeneraciones();
		}
		int numFramesGen = ventana.getNumFramesGen();
		if(numFramesGen < entidades.getTiempoVida()) {
			entidades.realizarCiclo();
			ventana.setNumFramesGen(++numFramesGen);
		} else {
			ventana.setNumFramesGen(0);
			entidades.evolucionar();
		}
		return entidades.getNumGeneraciones();
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

