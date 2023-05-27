package modelo.entidades;

import processing.core.PVector;
import java.util.Random;

public class ADN {
	
	private PVector[] genes;
	private float fuerzaMax = 0.8f;
	private float fuerzaMin = 0.1f;
	private Random random = new Random();
	
	public ADN (PVector[] genes) {
		this.genes = genes;
	}
	
	public ADN (int numFuerzas) {
		genes = generarGenesAleatorios(numFuerzas);
	}

	public void generarGenAleatorio(PVector gen) {
		gen = PVector.random2D().mult(random.nextFloat(fuerzaMin, fuerzaMax));
	}
	
	private PVector[] generarGenesAleatorios(int numFuerzas) {
		PVector[] genesAleatorios = new PVector[numFuerzas];
		for (int i=0; i < genesAleatorios.length; i++) {
			genesAleatorios[i] = PVector.random2D().mult(random.nextFloat(fuerzaMin, fuerzaMax));
		}
		return genesAleatorios;
	}
	
	public PVector[] getGenes() {
		return genes;
	}

}
