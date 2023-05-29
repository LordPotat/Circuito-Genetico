package modelo.componentes;

import processing.core.PVector;
import vista.ventana_grafica.Ventana;

/**
 * Elemento del circuito que las entidades deben evitar en su camino hacia la meta.
 * Contiene colisiones para detectar cuando alguna entidad ha chocado con �l.
 * @author Alberto
 */
public class Obstaculo extends Colisionable {

	private float angulo;

	/**
	 * Constructor
	 * @param posicion en la que se sit�a en la ventana
	 * @param ancho del rect�ngulo 
	 * @param alto del rect�ngulo
	 * @param angulo de rotaci�n del rect�ngulo 
	 */
	public Obstaculo(PVector posicion, float ancho, float alto, float angulo) {
		super(posicion, ancho, alto);
		this.angulo = Ventana.radians(angulo); //Convierte grados en radianes
	}
	
	/**
	 * Determina si el obst�culo contiene la posici�n actual de una entidad, para saber si colisiona
	 * o no con �l.
	 * @param posEntidad: el vector de posici�n de la entidad
	 * @return si la entidad colisiona o no con el obst�culo
	 */
	@Override
	public boolean chocaConEntidad(PVector posEntidad) {
		//TODO Mejorar las colisiones para que pueda detectarlas estando rotado en un �ngulo
		float esquinaX = posicion.x - ancho / 2;
		float esquinaY = posicion.y - alto / 2;
		boolean contieneEnX = posEntidad.x >= esquinaX && posEntidad.x <= esquinaX + ancho;
		boolean contieneEnY = posEntidad.y >= esquinaY && posEntidad.y <= esquinaY + alto;
		return contieneEnX && contieneEnY;
	}
	

	public float getAngulo() {
		return angulo;
	}

	public void setAngulo(float angulo) {
		this.angulo = angulo;
	}

}
