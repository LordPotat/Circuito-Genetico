package modelo.entidades;

import java.util.Random;

import processing.core.PVector;

public class ADN {
	
	private PVector[] genes;
	private float fuerzamax = 0.1f;

	public ADN (int numFuerzas) {
		this.genes = new PVector[numFuerzas];
		for (int i=0; i < this.genes.length; i++) {
			generarFuerzaAleatoria(i);
		}
	}

	public ADN (PVector[] genes) {
		this.genes = genes;
	}
	
	private void generarFuerzaAleatoria(int numGen) {
		genes[numGen] = PVector.random2D();
		genes[numGen].mult(new Random().nextFloat(0, fuerzamax));
	}

	
	public void generarFuerzaAleatoria(PVector gen) {
		gen = PVector.random2D();
		gen.mult(new Random().nextFloat(0, fuerzamax));
	}

	public PVector[] getGenes() {
		return genes;
	}

}
