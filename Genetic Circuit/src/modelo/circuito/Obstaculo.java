package modelo.circuito;

import processing.core.PMatrix2D;
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
		//Si el �ngulo es 0, no hay rotaci�n y se puede resolver de manera m�s sencilla y eficiente
		if(angulo == 0) {
			return colisionSinRotacion(posEntidad);
		} else {
			return colisionConRotacion(posEntidad);
		}
	}

	/**
	 * Determina si el rect�ngulo del obst�culo contiene la posici�n de la entidad en caso de que
	 * su �ngulo de rotaci�n sea 0. Este m�todo asume que el obst�culo es un rect�ngulo y que
	 * la posicion de la entidad es donde va a chocar exactamente (la punta de un tri�ngulo)
	 * @param posEntidad: el vector de posici�n de la entidad
	 * @return si la entidad colisiona o no con el obst�culo
	 */
	private boolean colisionSinRotacion(PVector posEntidad) {
		/* Obtiene la coordenada en el eje X de la esquina superior izquierda a partir de
		 * restarle la mitad del ancho, ya que la posici�n del rect�ngulo es su centro.
		 * Con este punto podemos determinar si colisiona horizontalmente con la entidad
		 */
		float esquinaX = posicion.x - ancho / 2;
		/* Repetimos lo mismo para obtener la coordenadda en el eje Y de la esquina superior
		 * izquierda. Con este punto determinamos si colisiona verticalmente con la entidad
		 */
		float esquinaY = posicion.y - alto / 2;
		/* Para comprobar si la entidad est� contenida en un eje, revisamos que la coordenada
		 * en el eje deseado de la entidad sea mayor o igual que la de la esquina que 
		 * corresponde del obst�culo, y despu�s comprobamos si es menor o igual que la
		 * coordenada que se obtiene al sumarle el ancho (X) o alto (Y) a la esquina.
		 * Esto quiere decir que la posici�n de la entidad coincide en ese eje con alg�n punto 
		 * del lado horizontal o vertical del rect�ngulo.
		 */
		boolean contieneEnX = posEntidad.x >= esquinaX && posEntidad.x <= esquinaX + ancho;
		boolean contieneEnY = posEntidad.y >= esquinaY && posEntidad.y <= esquinaY + alto;
		//Si la entidad est� contenida tanto en el eje X como el eje Y, hay colisi�n
		return contieneEnX && contieneEnY;
	}
	
	/**
	 * Determina si el rect�ngulo del obst�culo contiene la posici�n de la entidad en caso de que
	 * su �ngulo de rotaci�n sea distinto a 0. Este m�todo asume que el obst�culo es un rect�ngulo y que
	 * la posicion de la entidad es donde va a chocar exactamente (la punta de un tri�ngulo)
	 * @param posEntidad: el vector de posici�n de la entidad
	 * @return si la entidad colisiona o no con el obst�culo
	 */
	private boolean colisionConRotacion(PVector posEntidad) {
		/* Calculamos la posici�n relativa de la entidad respecto al centro del rect�ngulo.
		 * Para ello tenemos que restar el vector del obst�culo al vector de la entidad.
		 * Ese vector resultante representa la separaci�n entre ambas posiciones
		 */
		PVector posRelativa = PVector.sub(posEntidad, posicion);
		/* Como el �ngulo est� rotado, queremos revertir esa rotaci�n para ver si se encuentra
		 * dentro de los l�mites del rect�ngulo conociendo ya su posici�n relativa al centro.
		 * Si el obst�culo est� rotado en x radianes, para invertir ese �ngulo habr� que rotarlo 
		 * en -x radianes.
	     * Obtenemos la posici�n relativa rotada inversamente para que est� en el �ngulo de los
	     * ejes original (0 radianes). Para ello, aplicamos a su vector una matriz de transformaci�n
	     * de rotaci�n, que implica multiplicar las siguientes matrices:
	     * https://es.wikipedia.org/wiki/Matriz_de_rotaci%C3%B3n
	     * ----------------------	---------------
	     * |cosAngulo senAngulo | x |posRelativa.x| 
	     * |-senAngulo cosAngulo|	|posRelativa.y|
	     * ----------------------	---------------
	     * Esto dar� como resultado un vector rotado en 0 radianes respecto al sistema de coordenadas
	     */
		//Podemos obtener esa matriz de rotaci�n a partir de rotar una matriz identidad en el �ngulo
	    PMatrix2D matrizRotacion = new PMatrix2D(); 
	    matrizRotacion.rotate(-angulo);
	    /* Aplicamos la matriz al vector para rotarlo y obtener las coordenadas del nuevo vector que
	     * parte de la posici�n relativa rotada en el �ngulo invertido
	     */
	    PVector posRotada = new PVector();
	    posRotada.x = posRelativa.x * matrizRotacion.m00 + posRelativa.y * matrizRotacion.m01;
	    posRotada.y = posRelativa.x * matrizRotacion.m10 + posRelativa.y * matrizRotacion.m11;
	    /* Como ahora estamos trabajando sobre el origen de coordenadas, si quisieramos comprobar
	     * los l�mites del rect�ngulo, asumimos que el centro es (0,0) y que por tanto sus esquinas
	     * son resultado de restarle y/o sumarle la mitad de su ancho y alto
	     */
	    float mitadAncho = ancho / 2;
	    float mitadAlto = alto / 2;
	    /* Ahora que tenemos las esquinas podemos comprobar si el punto relativo con la rotaci�n
	     * revertida se encuentra dentro de los l�mites del rect�ngulo asumiendo que no se ha 
	     * aplicado ninguna transformaci�n sobre este (traslaci�n y rotaci�n). Ser� la misma 
	     * operaci�n que har�amos si no estuviese rotado el obst�culo.
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
