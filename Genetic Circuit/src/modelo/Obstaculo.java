package modelo;

import processing.core.PVector;
import vista.ventana_grafica.Ventana;

public class Obstaculo {

	private PVector posicion;
	private float ancho, alto, angulo;

	public Obstaculo(PVector posicion, float ancho, float alto, float angulo) {
		this.posicion = posicion;
		this.ancho = ancho;
		this.alto = alto;
		this.angulo = Ventana.radians(angulo);
	}
	
	public boolean chocaConEntidad(PVector posEntidad) {
		float esquinaX = posicion.x - ancho / 2;
		float esquinaY = posicion.y - alto / 2;
		boolean contieneEnX = posEntidad.x >= esquinaX && posEntidad.x <= esquinaX + ancho;
		boolean contieneEnY = posEntidad.y >= esquinaY && posEntidad.y <= esquinaY + alto;
		return contieneEnX && contieneEnY;
	}
	
	public PVector getPosicion() {
		return posicion;
	}

	public float getAncho() {
		return ancho;
	}

	public float getAlto() {
		return alto;
	}

	public float getAngulo() {
		return angulo;
	}

}
