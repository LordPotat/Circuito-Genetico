package modelo;

import processing.core.PVector;

public class Meta {
	
	private static PVector posicion;
	private static float ancho, alto;
	private static float semiejeMayor, semiejeMenor;

	private Meta(PVector posicion, float ancho, float alto) {
		Meta.posicion = posicion;
		Meta.ancho = ancho;
		Meta.alto = alto;
		calcularSemiejes();
	}
	
	public static boolean chocaConEntidad(PVector posEntidad) {
		float distanciaCentroX = posEntidad.x - Meta.posicion.x;
		float distanciaCentroY = posEntidad.y - Meta.posicion.y;
		double distancia = (Math.pow(distanciaCentroX,2)) / 
				(Math.pow(semiejeMayor,2)) + (Math.pow(distanciaCentroY,2)) / (Math.pow(semiejeMenor,2));
		return distancia <= 1;
	}
	
	private static void calcularSemiejes() {
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
	
	public static PVector getPosicion() {
		return posicion;
	}
	
}
