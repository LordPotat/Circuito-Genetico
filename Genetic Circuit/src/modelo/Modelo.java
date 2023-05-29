package modelo;

import java.util.ArrayList;
import java.util.HashMap;

import controlador.Controlador;
import modelo.circuitos.Circuito;
import modelo.componentes.Meta;
import modelo.componentes.Obstaculo;
import modelo.entidades.Poblacion;
import processing.core.PVector;

public class Modelo {
	
	private Controlador controlador;
	private Circuito circuito;
	private Poblacion poblacionEntidades;
	private Meta meta;
	private Obstaculo[] obstaculos;
	
	public Modelo(Controlador controlador) {
		this.controlador = controlador;
		circuito = new Circuito();
	}

	public Poblacion getPoblacion() {
		return poblacionEntidades;
	}
	
	public void setPoblacionEntidades(HashMap<String, Integer> poblacionParams, PVector posInicial) {
		poblacionEntidades = new Poblacion(this, poblacionParams, posInicial);
	}
	
	public Meta getMeta() {
		return meta;
	}

	public void setMeta(HashMap<String, Object> metaParams) {
		meta = new Meta(
				(PVector) metaParams.get("Posicion"),
				(float) metaParams.get("Ancho"), 
				(float) metaParams.get("Alto")
		);
	}
	
	public Obstaculo[] getObstaculos() {
		return obstaculos;
	}

	public void setObstaculos(ArrayList<HashMap<String, Object>> obstaculosParams) {
		obstaculos = new Obstaculo[obstaculosParams.size()];
		for(int i=0; i < obstaculos.length; i++) {
			HashMap<String, Object> params = obstaculosParams.get(i);
			obstaculos[i] = new Obstaculo(
					(PVector) params.get("Posicion"),
					(float) params.get("Ancho"),
					(float) params.get("Alto"),
					(float) params.get("Angulo")
			);
		}
	}
	
	public Controlador getControlador() {
		return controlador;
	}

	public Circuito getCircuito() {
		return circuito;
	}

	public void setCircuito(Circuito circuito) {
		this.circuito = circuito;
	}
	
	
}
