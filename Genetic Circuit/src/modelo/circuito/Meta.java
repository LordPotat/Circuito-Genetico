package modelo.circuito;

import processing.core.PVector;

/**
 * El elemento del circuito que actuará como objetivo a alcanzar por las entidades.
 * Contiene colisiones para detectar cuando alguna entidad ha llegado a ella
 * @author Alberto
 */
public class Meta extends Colisionable{
	
	private float semiejeHorizontal, semiejeVertical;

	/**
	 * Constructor
	 * @param posicion en la que se sitúa en la ventana
	 * @param ancho: tamaño del eje horizontal 
	 * @param alto: tamaño del eje vertical
	 */
	public Meta(PVector posicion, float ancho, float alto) {
		super(posicion, ancho, alto);
		//Los semiejes son equivalentes a la mitad de los ejes (ancho y alto)
		semiejeHorizontal = ancho / 2;
		semiejeVertical = alto / 2;
	}
	
	/**
	 * Determina si la meta contiene la posición actual de una entidad, para saber si colisiona
	 * o no con ella.
	 * @param posEntidad: el vector de posición de la entidad
	 * @return si contiene o no la posicion de la entidad
	 */
	@Override
	public boolean chocaConEntidad(PVector posEntidad) {
		//Distancia de la entidad al centro de la elipse en ambos ejes
		float distanciaCentroX = posEntidad.x - this.posicion.x;
		float distanciaCentroY = posEntidad.y - this.posicion.y;
		/* Obtiene la distancia real de la entidad respecto a la superficie de la elipse.
		 * La fórmula se basa en la ecuación de la elipse
		 * https://es.wikipedia.org/wiki/Semieje_mayor_y_semieje_menor
		 * Sustitye x-h por distanciaCentroX e y-k por distanciaCentroY, y a por el 
		 * semieje horizontal y b el semieje vertical,
		 */
		double distancia = (Math.pow(distanciaCentroX,2)) / (Math.pow(semiejeHorizontal,2))
				+ (Math.pow(distanciaCentroY,2)) / (Math.pow(semiejeVertical,2));
		/* Si la distancia es menor o igual que 1, quiere decir que está contenido en la elipse,
		 * ya que la fórmula sirve para calcular todos los puntos que forman la elipse tanto 
		 * en el borde como en su superficie
		 */
		return distancia <= 1;
	}

	public float getSemiejeHorizontal() {
		return semiejeHorizontal;
	}

	public float getSemiejeVertical() {
		return semiejeVertical;
	}

	@Override
	public void setAncho(float ancho) {
		this.ancho = ancho;
		semiejeHorizontal = ancho/2;
	}

	@Override
	public void setAlto(float alto) {
		this.alto = alto;
		semiejeVertical = alto/2;
	}
	
}
