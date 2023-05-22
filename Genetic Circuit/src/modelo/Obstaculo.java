package modelo;

import processing.core.PVector;

public class Obstaculo {

	private PVector posicion;
	private float ancho, alto;

	public Obstaculo() {
		
	}
	
	public boolean chocaConEntidad(PVector posEntidad) {
		boolean contieneEnX = posEntidad.x > this.posicion.x && posEntidad.x < this.posicion.x + ancho;
		boolean contieneEnY = posEntidad.y > this.posicion.y && posEntidad.y < this.posicion.y + alto;
		return contieneEnX && contieneEnY;
	}
	
	public PVector getPosicion() {
		return posicion;
	}

}
