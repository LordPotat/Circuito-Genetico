package modelo.entidades;

import processing.core.PVector;
import vista.ventana_grafica.Ventana;

import java.util.Random;

public class ADN {
	
	private PVector[] genes;
	private float fuerzaMax = 1f;
	private float fuerzaMin = 0.1f;
	private Random rng = new Random();
	
	public ADN (int numFuerzas) {
		genes = new PVector[numFuerzas];
		for (int i=0; i < genes.length; i++) {
//			genes[i] = PVector.random2D();
			genes[i] = PVector.fromAngle(rng.nextFloat(0, Ventana.TWO_PI));
			genes[i].mult(rng.nextFloat(fuerzaMin, fuerzaMax));
		}
	}

	public ADN (PVector[] genes) {
		this.genes = genes;
	}

	
	public void generarFuerzaAleatoria(PVector gen) {
		//gen = PVector.random2D();
		gen = PVector.fromAngle(rng.nextFloat(0, Ventana.TWO_PI));
		gen.mult(rng.nextFloat(fuerzaMin, fuerzaMax));
	}

	public PVector[] getGenes() {
		return genes;
	}

}
