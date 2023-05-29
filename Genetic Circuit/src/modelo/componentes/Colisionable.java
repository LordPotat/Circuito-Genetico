package modelo.componentes;

import processing.core.PVector;

/**
 * Elemento del circuito con el que interaccionan las entidades
 * Contiene colisiones para detectar cuando alguna entidad ha chocado con él.
 * @author Alberto
 */
public abstract class Colisionable {
	
	protected PVector posicion;
	protected float ancho, alto;
	
	/**
	 * Constructor
	 * @param posicion en la que se sitúa en la ventana
	 * @param ancho de la figura 
	 * @param alto de la figura
	 */
	protected Colisionable(PVector posicion, float ancho, float alto) {
		this.posicion = posicion;
		this.ancho = ancho;
		this.alto = alto;
	}
	
	/**
	 * Determina si el elemento contiene la posición actual de una entidad, para saber si colisiona
	 * o no con él.
	 * @param posEntidad: el vector de posición de la entidad
	 * @return si contiene o no la posicion de la entidad
	 */
	public abstract boolean chocaConEntidad(PVector posEntidad);

	public PVector getPosicion() {
		return posicion;
	}

	public void setPosicion(PVector posicion) {
		this.posicion = posicion;
	}

	public float getAncho() {
		return ancho;
	}

	public void setAncho(float ancho) {
		this.ancho = ancho;
	}

	public float getAlto() {
		return alto;
	}

	public void setAlto(float alto) {
		this.alto = alto;
	}
}
