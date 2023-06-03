package modelo;

import java.util.ArrayList;
import java.util.HashMap;

import controlador.Controlador;
import modelo.circuito.Circuito;
import modelo.circuito.Meta;
import modelo.circuito.Obstaculo;
import modelo.entidades.Poblacion;
import processing.core.PVector;

public class Modelo {
	
	/**
	 * Interfaz con la que el modelo puede comunicarse con la vista
	 */
	private Controlador controlador;
	/**
	 * Circuito que define la meta, obstáculos, y dónde aparecen las entidades
	 */
	private Circuito circuito;
	/**
	 * Población que agrupa las entidades que interaccionan con el circuito
	 */
	private Poblacion poblacionEntidades;
	/**
	 * Meta que contenrá el circuito
	 */
	private Meta meta;
	/**
	 * Colección de obstáculos que contendrá el circuito
	 */
	private Obstaculo[] obstaculos;
	
	/**
	 * Inicializa el modelo de datos pasándole el controlador que utilizará para alterar
	 * el comportamiento del programa 
	 * @param controlador con el que se comunica con el resto del programa
	 */
	public Modelo(Controlador controlador) {
		this.controlador = controlador;
	}

	public Circuito getCircuito() {
		return circuito;
	}
	
	/**
	 * Reemplaza el circuito actual por uno nuevo que ha cargado desde el fichero
	 * correspondiente
	 * @param circuito que obtiene tras cargarlo
	 */
	public void setCircuito(Circuito circuito) {
		this.circuito = circuito;
	}
	
	public Meta getMeta() {
		return meta;
	}

	/**
	 * Reemplaza la meta por una nueva cuyas propiedades vienen determinadas
	 * por un mapa de parámetros que recibe
	 * @param metaParams que determinan los atributos de la meta
	 */
	public void setMeta(HashMap<String, Object> metaParams) {
		/* Crea un objeto Meta con su posición, ancho y alto. Debe castear
		 * cada atributo a su tipo correspondiente ya que se consideran objetos genéricos
		 */
		meta = new Meta(
				(PVector) metaParams.get("Posicion"),
				(float) metaParams.get("Ancho"), 
				(float) metaParams.get("Alto")
		);
	}
	
	public Obstaculo[] getObstaculos() {
		return obstaculos;
	}

	/**
	 * Reemplaza la colección de obstáculos por una nueva cuyas propiedades individuales
	 * vienen determinadas por cada mapa contenido en la colección de parámetros que recibe
	 * @param obstaculosParams que determinan los atributos de cada obstáculo
	 */
	public void setObstaculos(ArrayList<HashMap<String, Object>> obstaculosParams) {
		//Inicializa tantos obstáculos como vengan incluidos en la colección
		obstaculos = new Obstaculo[obstaculosParams.size()];
		//Establece las propiedades que tiene cada uno con los datos de la colección
		for(int i=0; i < obstaculos.length; i++) {
			inicializarObstaculo(obstaculosParams.get(i), i);
		}
	}
	
	public Poblacion getPoblacion() {
		return poblacionEntidades;
	}
	
	/**
	 * Reemplaza la población por una nueva cuyas propiedades vienen determinadas
	 * por un mapa de parámetros que recibe 
	 * @param poblacionParams mapa con los atributos de la población
	 * @param posInicial en la que deberán spawnear todas las entidades
	 */
	public void setPoblacionEntidades(HashMap<String, Integer> poblacionParams, PVector posInicial) {
		//Crea un nuevo objeto Población pasándole el modelo como contexto y los argumentos necesarios
		poblacionEntidades = new Poblacion(this, poblacionParams, posInicial);
	}
	
	/**
	 * Establece las propiedades de un obstáculo a partir del mapa que le corresponde
	 * @param obstaculoParams: mapa con los atributos del obstáculo
	 * @param i el índice que ocupa el obstáculo en la colección
	 */
	private void inicializarObstaculo(HashMap<String, Object> obstaculoParams, int i) {
		HashMap<String, Object> params = obstaculoParams;
		/* Crea un objeto Obstáculo con su posición, ancho, alto y ángulo. Debe castear
		 * cada atributo a su tipo correspondiente ya que se consideran objetos genéricos
		 */
		obstaculos[i] = new Obstaculo(
				(PVector) params.get("Posicion"),
				(float) params.get("Ancho"),
				(float) params.get("Alto"),
				(float) params.get("Angulo")
		);
	}
	
	public Controlador getControlador() {
		return controlador;
	}
	
}
