package modelo;

import processing.core.PVector;

public class Meta {
	
	private PVector posicion;
	private float ancho, alto;
	private float semiejeMayor, semiejeMenor;

	public Meta(PVector posicion, float ancho, float alto) {
		this.posicion = posicion;
		this.ancho = ancho;
		this.alto = alto;
		calcularSemiejes();
	}
	
	public boolean chocaConEntidad(PVector posEntidad) {
		float distanciaCentroX = posEntidad.x - this.posicion.x;
		float distanciaCentroY = posEntidad.y - this.posicion.y;
		double distancia = (Math.pow(distanciaCentroX,2)) / 
				(Math.pow(semiejeMayor,2)) + (Math.pow(distanciaCentroY,2)) / (Math.pow(semiejeMenor,2));
		return distancia <= 1;
	}
	
	private void calcularSemiejes() {
		if(ancho > alto) {
			semiejeMayor = ancho / 2;
			semiejeMenor = alto / 2;
		} else if(ancho < alto) {
			semiejeMayor = alto / 2;
			semiejeMenor = ancho / 2;
		} else {
			float semieje = ancho / 2;
			semiejeMayor = semieje;
			semiejeMenor = semieje;
		}
	}
	
	public PVector getPosicion() {
		return posicion;
	}
	
}
