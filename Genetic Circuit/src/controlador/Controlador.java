package controlador;

import java.util.HashMap;
import modelo.Modelo;
import modelo.circuitos.Circuito;
import modelo.entidades.Entidad;
import modelo.entidades.Poblacion;
import vista.Vista;
import vista.ventana_grafica.Ventana;

/** 
 * Se encarga de iniciar y manipular el flujo del programa. Contiene la vista y el modelo de datos.
 * 
 * @author Alberto P�rez
 */
public class Controlador {
	
	private Vista vista;
	private Modelo modelo;
	
	/** 
	 * Inicia el modelo de datos y la vista con la propia instancia para que accedan al controlador
	 */
	public Controlador() {
		vista = new Vista(this);
		modelo = new Modelo(this);
	}

	/** 
	 * Inicia los objetos del modelo de datos: la meta, obstaculos y poblaci�n
	 * Obtiene los datos almacenados en el circuito escogido para configurar sus par�metros
	 */
	public void iniciar() {
		Ventana ventana = vista.getVentana();
		Circuito circuito = modelo.getCircuito();
		
		modelo.setMeta(circuito.setupMeta(ventana));
		modelo.setObstaculos(circuito.setupObstaculos(ventana));
		modelo.setPoblacionEntidades(setupPoblacion(), circuito.setSpawn(ventana));
	}

	/**
	 * Establece los par�metros iniciales del proceso evolutivo de la poblaci�n
	 * @return el mapa con los par�metros 
	 */
	private HashMap<String, Integer> setupPoblacion() {
		HashMap<String, Integer> poblacionParams = new HashMap<String, Integer>();
		poblacionParams.put("NumEntidades", 5000);
		poblacionParams.put("TasaMutacion", 20);	
		poblacionParams.put("TiempoVida", 200); //TODO controlar que tiempoVida !<= tiempoObjetivo
		return poblacionParams;
	}

	/**
	 * Realiza todo el proceso evolutivo de la poblaci�n. Comprueba si se ha cumplido
	 * el objetivo para pararlo y mostrar la ruta �ptima. En caso contrario, debe permitir
	 * a la poblaci�n realizar su ciclo de vida hasta que acabe y tenga que evolucionar.
	 * En ese momento llamara a la funci�n con los pasos del algoritmo gen�tico necesarios
	 * @return el numero de generaciones actual de la poblaci�n
	 */
	
	public int manipularPoblacion() {
		Poblacion entidades = modelo.getPoblacion();
		Ventana ventana = vista.getVentana();
		
		/* Comprueba si las poblaci�n ha cumplido el objetivo y sale de la funci�n 
		 * si es el caso despu�s de mostrar la ruta �ptima, para no continuar el proceso
		 */
		if(entidades.isObjetivoCumplido()) {
			mostrarRutaOptima(entidades.getMejorEntidad());
			return entidades.getNumGeneraciones();
		}
		//N�mero de frames que han pasado desde el inicio de su ciclo de vida
		int numFramesGen = ventana.getNumFramesGen();
		//Si todav�a le queda tiempo de vida a la poblaci�n, realiza un ciclo de ejecuci�n
		if(numFramesGen < entidades.getTiempoVida()) {
			entidades.realizarCiclo();
			ventana.setNumFramesGen(++numFramesGen);
		} 
		//De lo contrario, la poblaci�n debe evolucionar y reiniciar su ciclo de vida
		else {
			ventana.setNumFramesGen(0);
			entidades.evolucionar();
		}
		
		return entidades.getNumGeneraciones();
	}
	
	/**
	 * Muestra en la ventana gr�fica la ruta �ptima (en el tiempo establecido) 
	 * desde el punto inicial de la poblaci�n hasta la meta
	 * @param mejorEntidad: la entidad que ha logrado el objetivo establecido en el circuito
	 */
	public void mostrarRutaOptima(Entidad mejorEntidad) {
		vista.getVentana().drawRutaOptima(mejorEntidad.getAdn().getGenes(), mejorEntidad.getTiempoObtenido());
		vista.getVentana().drawEntidad(mejorEntidad.getPosicion(), mejorEntidad.getVelocidad());
	}
	
	/**
	 * Muestra una entidad en la ventana gr�fica en la posici�n y direcci�n actual
	 * @param entidad que se debe mostrar
	 */
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

