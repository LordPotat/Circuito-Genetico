package modelo.entidades;

import processing.core.PVector;
import java.util.Random;

/**
 * Genotipo de la entidad, que determina qué movimientos podrá realizar en su ciclo de vida
 * @author Alberto
 */
public class ADN {
	
	/**
	 * Magnitud máxima que pueden tener los vectores
	 */
	private static final float FUERZA_MAX = 0.8f;
	/**
	 * Magnitud MÍNIMA que pueden tener los vectores
	 */
	private static final float FUERZA_MIN = 0.1f;
	
	/**
	 * Genes del genotipo, un array de vectores que actúan como fuerzas
	 */
	private PVector[] genes;
	
	private Random random = new Random();
	
	/**
	 * Constructor que toma un array de vectores que harán de genes de la entidad
	 * @param genes
	 */
	public ADN (PVector[] genes) {
		this.genes = genes;
	}
	
	/**
	 * Constructor que genera aleatoriamente tantos genes como fuerzas deberá aplicar.
	 * @param numFuerzas: viene determinado por el tiempo de vida de la entidad en frames
	 */
	public ADN (int numFuerzas) {
		genes = generarGenesAleatorios(numFuerzas);
	}

	/**
	 * Modifica un gen de forma que se convierte en un vector aleatorio.
	 * El vector puede tener cualquier dirección y sentido, y su magnitud (la intensidad de
	 * la fuerza) tiene un rango entre el valor mínimo y máximo especificado
	 * @param gen
	 */
	public void generarGenAleatorio(PVector gen) {
		/* Random2D devuelve un vector unitario (con una magnitud de 1) en una dirección y sentido
		 * aleatorio. Para darle una magnitud aleatoria (dentro del rango especificado), hay
		 * que mutiplicar el vector unitario por el escalar, que determina el "tamaño" del vector
		 */
		gen = PVector.random2D().mult(random.nextFloat(FUERZA_MIN, FUERZA_MAX));
	}
	
	/**
	 * Genera un array de vectores con el tamaño del número de fuerzas a aplicar.
	 * Cada elemento es un nuevo vector con dirección, sentido y mangitud aleatorios.
	 * @param numFuerzas
	 * @return el array de vectores que harán de genes
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
