package modelo.entidades;

import java.util.Random;

import processing.core.PVector;

public class ADN {
	
	private PVector[] genes;
	private float fuerzamax = 0.1f;

	public ADN (int num) {
		genes = new PVector[num];
		for (int i = 0; i < genes.length; i++) {
			genes[i] = PVector.random2D();
			genes[i].mult(new Random().nextFloat(0, fuerzamax));
		}
	}

	public PVector[] getGenes() {
		return genes;
	}

	public void setGenes(PVector[] genes) {
		this.genes = genes;
	}

}
