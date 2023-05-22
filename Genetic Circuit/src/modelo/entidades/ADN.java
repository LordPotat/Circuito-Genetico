package modelo.entidades;

import java.util.Random;

import processing.core.PVector;

public class ADN {
	
	private PVector[] genes;
	private float fuerzamax = 0.1f;

	public ADN (int numFuerzas, boolean esPrimeraGen) {
		genes = new PVector[numFuerzas];
		if(esPrimeraGen) {
			for (PVector gen : genes) {
				generarFuerzaAleatoria(gen);
			}
		}
	}

	public void generarFuerzaAleatoria(PVector gen) {
		gen = PVector.random2D();
		gen.mult(new Random().nextFloat(0, fuerzamax));
	}

	public PVector[] getGenes() {
		return genes;
	}

}
