package modelo.circuito;

import processing.core.PMatrix2D;
import processing.core.PVector;
import vista.ventana_grafica.Ventana;

/**
 * Elemento del circuito que las entidades deben evitar en su camino hacia la meta.
 * Contiene colisiones para detectar cuando alguna entidad ha chocado con él.
 * @author Alberto
 */
public class Obstaculo extends Colisionable {

	private float angulo;

	/**
	 * Constructor
	 * @param posicion en la que se sitúa en la ventana
	 * @param ancho del rectángulo 
	 * @param alto del rectángulo
	 * @param angulo de rotación del rectángulo 
	 */
	public Obstaculo(PVector posicion, float ancho, float alto, float angulo) {
		super(posicion, ancho, alto);
		this.angulo = Ventana.radians(angulo); //Convierte grados en radianes
	}
	
	/**
	 * Determina si el obstáculo contiene la posición actual de una entidad, para saber si colisiona
	 * o no con él.
	 * @param posEntidad: el vector de posición de la entidad
	 * @return si la entidad colisiona o no con el obstáculo
	 */
	@Override
	public boolean chocaConEntidad(PVector posEntidad) {
		//Si el ángulo es 0, no hay rotación y se puede resolver de manera más sencilla y eficiente
		if(angulo == 0) {
			return colisionSinRotacion(posEntidad);
		} else {
			return colisionConRotacion(posEntidad);
		}
	}

	/**
	 * Determina si el rectángulo del obstáculo contiene la posición de la entidad en caso de que
	 * su ángulo de rotación sea 0. Este método asume que el obstáculo es un rectángulo y que
	 * la posicion de la entidad es donde va a chocar exactamente (la punta de un triángulo)
	 * @param posEntidad: el vector de posición de la entidad
	 * @return si la entidad colisiona o no con el obstáculo
	 */
	private boolean colisionSinRotacion(PVector posEntidad) {
		/* Obtiene la coordenada en el eje X de la esquina superior izquierda a partir de
		 * restarle la mitad del ancho, ya que la posición del rectángulo es su centro.
		 * Con este punto podemos determinar si colisiona horizontalmente con la entidad
		 */
		float esquinaX = posicion.x - ancho / 2;
		/* Repetimos lo mismo para obtener la coordenadda en el eje Y de la esquina superior
		 * izquierda. Con este punto determinamos si colisiona verticalmente con la entidad
		 */
		float esquinaY = posicion.y - alto / 2;
		/* Para comprobar si la entidad está contenida en un eje, revisamos que la coordenada
		 * en el eje deseado de la entidad sea mayor o igual que la de la esquina que 
		 * corresponde del obstáculo, y después comprobamos si es menor o igual que la
		 * coordenada que se obtiene al sumarle el ancho (X) o alto (Y) a la esquina.
		 * Esto quiere decir que la posición de la entidad coincide en ese eje con algún punto 
		 * del lado horizontal o vertical del rectángulo.
		 */
		boolean contieneEnX = posEntidad.x >= esquinaX && posEntidad.x <= esquinaX + ancho;
		boolean contieneEnY = posEntidad.y >= esquinaY && posEntidad.y <= esquinaY + alto;
		//Si la entidad está contenida tanto en el eje X como el eje Y, hay colisión
		return contieneEnX && contieneEnY;
	}
	
	/**
	 * Determina si el rectángulo del obstáculo contiene la posición de la entidad en caso de que
	 * su ángulo de rotación sea distinto a 0. Este método asume que el obstáculo es un rectángulo y que
	 * la posicion de la entidad es donde va a chocar exactamente (la punta de un triángulo)
	 * @param posEntidad: el vector de posición de la entidad
	 * @return si la entidad colisiona o no con el obstáculo
	 */
	private boolean colisionConRotacion(PVector posEntidad) {
		/* Calculamos la posición relativa de la entidad respecto al centro del rectángulo.
		 * Para ello tenemos que restar el vector del obstáculo al vector de la entidad.
		 * Ese vector resultante representa la separación entre ambas posiciones
		 */
		PVector posRelativa = PVector.sub(posEntidad, posicion);
		/* Como el ángulo está rotado, queremos revertir esa rotación para ver si se encuentra
		 * dentro de los límites del rectángulo conociendo ya su posición relativa al centro.
		 * Si el obstáculo está rotado en x radianes, para invertir ese ángulo habrá que rotarlo 
		 * en -x radianes.
	     * Obtenemos la posición relativa rotada inversamente para que esté en el ángulo de los
	     * ejes original (0 radianes). Para ello, aplicamos a su vector una matriz de transformación
	     * de rotación, que implica multiplicar las siguientes matrices:
	     * https://es.wikipedia.org/wiki/Matriz_de_rotaci%C3%B3n
	     * ----------------------	---------------
	     * |cosAngulo senAngulo | x |posRelativa.x| 
	     * |-senAngulo cosAngulo|	|posRelativa.y|
	     * ----------------------	---------------
	     * Esto dará como resultado un vector rotado en 0 radianes respecto al sistema de coordenadas
	     */
		//Podemos obtener esa matriz de rotación a partir de rotar una matriz identidad en el ángulo
	    PMatrix2D matrizRotacion = new PMatrix2D(); 
	    matrizRotacion.rotate(-angulo);
	    /* Aplicamos la matriz al vector para rotarlo y obtener las coordenadas del nuevo vector que
	     * parte de la posición relativa rotada en el ángulo invertido
	     */
	    PVector posRotada = new PVector();
	    posRotada.x = posRelativa.x * matrizRotacion.m00 + posRelativa.y * matrizRotacion.m01;
	    posRotada.y = posRelativa.x * matrizRotacion.m10 + posRelativa.y * matrizRotacion.m11;
	    /* Como ahora estamos trabajando sobre el origen de coordenadas, si quisieramos comprobar
	     * los límites del rectángulo, asumimos que el centro es (0,0) y que por tanto sus esquinas
	     * son resultado de restarle y/o sumarle la mitad de su ancho y alto
	     */
	    float mitadAncho = ancho / 2;
	    float mitadAlto = alto / 2;
	    /* Ahora que tenemos las esquinas podemos comprobar si el punto relativo con la rotación
	     * revertida se encuentra dentro de los límites del rectángulo asumiendo que no se ha 
	     * aplicado ninguna transformación sobre este (traslación y rotación). Será la misma 
	     * operación que haríamos si no estuviese rotado el obstáculo.
	     */
	    boolean colisionaX = posRotada.x >= -mitadAncho && posRotada.x <= mitadAncho; 
	    boolean colisionaY = posRotada.y >= -mitadAlto && posRotada.y <= mitadAlto;
	    return colisionaX && colisionaY;
	}
	
	public float getAngulo() {
		return angulo;
	}

	public void setAngulo(float angulo) {
		this.angulo = angulo;
	}

}
