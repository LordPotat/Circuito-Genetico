package modelo;

import java.util.ArrayList;
import java.util.HashMap;

import controlador.Controlador;
import modelo.entidades.Poblacion;
import processing.core.PVector;

public class Modelo {
	
	private Controlador controlador;
	private Poblacion poblacionEntidades;
	private Meta meta;
	private Obstaculo[] obstaculos;
	
	public Modelo(Controlador controlador, HashMap<String, Object> metaParams, 
			int numObstaculos, ArrayList<HashMap<String, Object>> obstaculosParams) {
		
		this.controlador = controlador;
		meta = new Meta(
				(PVector) metaParams.get("Posicion"),
				(float) metaParams.get("Ancho"), 
				(float) metaParams.get("Alto")
		);
		obstaculos = new Obstaculo[numObstaculos];
		//initObstaculos(obstaculosParams);
	}

	private void initObstaculos(ArrayList<HashMap<String, Object>> obstaculosParams) {
		for(int i=0; i < obstaculos.length; i++) {
			HashMap<String, ?> params = obstaculosParams.get(i);
			obstaculos[i] = new Obstaculo(
					(PVector) params.get("Posicion"),
					(float) params.get("Ancho"),
					(float) params.get("Alto"),
					(float) params.get("Angulo")
			);
		}
	}

	public Poblacion getPoblacionEntidades() {
		return poblacionEntidades;
	}
	
//	public void setPoblacionEntidades(int numEntidades, int tasaMutacion, int tiempoVida, PVector posInicial) {
//		poblacionEntidades = new Poblacion(this, numEntidades, tasaMutacion, tiempoVida, posInicial);
//	}
	
	public void setPoblacionEntidades(HashMap<String, Integer> poblacionParams, PVector posInicial) {
		poblacionEntidades = new Poblacion(this, poblacionParams, posInicial);
	}
	
	public Meta getMeta() {
		return meta;
	}

	public Obstaculo[] getObstaculos() {
		return obstaculos;
	}

	public Controlador getControlador() {
		return controlador;
	}
	
	
}
