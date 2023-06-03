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
	 * Circuito que define la meta, obst�culos, y d�nde aparecen las entidades
	 */
	private Circuito circuito;
	/**
	 * Poblaci�n que agrupa las entidades que interaccionan con el circuito
	 */
	private Poblacion poblacionEntidades;
	/**
	 * Meta que contenr� el circuito
	 */
	private Meta meta;
	/**
	 * Colecci�n de obst�culos que contendr� el circuito
	 */
	private Obstaculo[] obstaculos;
	
	/**
	 * Inicializa el modelo de datos pas�ndole el controlador que utilizar� para alterar
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
	 * por un mapa de par�metros que recibe
	 * @param metaParams que determinan los atributos de la meta
	 */
	public void setMeta(HashMap<String, Object> metaParams) {
		/* Crea un objeto Meta con su posici�n, ancho y alto. Debe castear
		 * cada atributo a su tipo correspondiente ya que se consideran objetos gen�ricos
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
	 * Reemplaza la colecci�n de obst�culos por una nueva cuyas propiedades individuales
	 * vienen determinadas por cada mapa contenido en la colecci�n de par�metros que recibe
	 * @param obstaculosParams que determinan los atributos de cada obst�culo
	 */
	public void setObstaculos(ArrayList<HashMap<String, Object>> obstaculosParams) {
		//Inicializa tantos obst�culos como vengan incluidos en la colecci�n
		obstaculos = new Obstaculo[obstaculosParams.size()];
		//Establece las propiedades que tiene cada uno con los datos de la colecci�n
		for(int i=0; i < obstaculos.length; i++) {
			inicializarObstaculo(obstaculosParams.get(i), i);
		}
	}
	
	public Poblacion getPoblacion() {
		return poblacionEntidades;
	}
	
	/**
	 * Reemplaza la poblaci�n por una nueva cuyas propiedades vienen determinadas
	 * por un mapa de par�metros que recibe 
	 * @param poblacionParams mapa con los atributos de la poblaci�n
	 * @param posInicial en la que deber�n spawnear todas las entidades
	 */
	public void setPoblacionEntidades(HashMap<String, Integer> poblacionParams, PVector posInicial) {
		//Crea un nuevo objeto Poblaci�n pas�ndole el modelo como contexto y los argumentos necesarios
		poblacionEntidades = new Poblacion(this, poblacionParams, posInicial);
	}
	
	/**
	 * Establece las propiedades de un obst�culo a partir del mapa que le corresponde
	 * @param obstaculoParams: mapa con los atributos del obst�culo
	 * @param i el �ndice que ocupa el obst�culo en la colecci�n
	 */
	private void inicializarObstaculo(HashMap<String, Object> obstaculoParams, int i) {
		HashMap<String, Object> params = obstaculoParams;
		/* Crea un objeto Obst�culo con su posici�n, ancho, alto y �ngulo. Debe castear
		 * cada atributo a su tipo correspondiente ya que se consideran objetos gen�ricos
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
