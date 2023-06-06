package modelo.entidades;

import processing.core.PVector;
import java.util.Random;

/**
 * Genotipo de la entidad, que determina qu� movimientos podr� realizar en su ciclo de vida
 * @author Alberto
 */
public class ADN {
	
	/**
	 * Magnitud m�xima que pueden tener los vectores
	 */
	private static final float FUERZA_MAX = 0.8f;
	/**
	 * Magnitud M�NIMA que pueden tener los vectores
	 */
	private static final float FUERZA_MIN = 0.1f;
	
	/**
	 * Genes del genotipo, un array de vectores que act�an como fuerzas
	 */
	private PVector[] genes;
	
	private Random random = new Random();
	
	/**
	 * Constructor que toma un array de vectores que har�n de genes de la entidad
	 * @param genes
	 */
	public ADN (PVector[] genes) {
		this.genes = genes;
	}
	
	/**
	 * Constructor que genera aleatoriamente tantos genes como fuerzas deber� aplicar.
	 * @param numFuerzas: viene determinado por el tiempo de vida de la entidad en frames
	 */
	public ADN (int numFuerzas) {
		genes = generarGenesAleatorios(numFuerzas);
	}

	/**
	 * Modifica un gen de forma que se convierte en un vector aleatorio.
	 * El vector puede tener cualquier direcci�n y sentido, y su magnitud (la intensidad de
	 * la fuerza) tiene un rango entre el valor m�nimo y m�ximo especificado
	 * @param gen
	 */
	public void generarGenAleatorio(PVector gen) {
		/* Random2D devuelve un vector unitario (con una magnitud de 1) en una direcci�n y sentido
		 * aleatorio. Para darle una magnitud aleatoria (dentro del rango especificado), hay
		 * que mutiplicar el vector unitario por el escalar, que determina el "tama�o" del vector
		 */
		gen = PVector.random2D().mult(random.nextFloat(FUERZA_MIN, FUERZA_MAX));
	}
	
	/**
	 * Genera un array de vectores con el tama�o del n�mero de fuerzas a aplicar.
	 * Cada elemento es un nuevo vector con direcci�n, sentido y mangitud aleatorios.
	 * @param numFuerzas
	 * @return el array de vectores que har�n de genes
	 */
	private PVector[] generarGenesAleatorios(int numFuerzas) {
		PVector[] genesAleatorios = new PVector[numFuerzas];
		for (int i=0; i < genesAleatorios.length; i++) {
			genesAleatorios[i] = PVector.random2D().mult(random.nextFloat(FUERZA_MIN, FUERZA_MAX));
		}
		return genesAleatorios;
	}
	
	public PVector[] getGenes() {
		return genes;
	}

}
