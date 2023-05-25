package modelo.entidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import modelo.Modelo;
import processing.core.PVector;

public class Poblacion {
	
	private Modelo contexto;
	private Entidad[] entidades;
	private ArrayList<Entidad> poolGenetico;
	private double tasaMutacion;
	private int tiempoVida;
	private int numGeneraciones;
	private PVector posInicial;
	
	private Random rng = new Random();
	
	public Poblacion(Modelo contexto, HashMap<String, Integer> poblacionParams, PVector posInicial) {
		this.contexto = contexto;
		entidades = new Entidad[poblacionParams.get("NumEntidades")];
		poolGenetico = new ArrayList<Entidad>();
		this.tasaMutacion = ((double) poblacionParams.get("TasaMutacion")) / 100;
		numGeneraciones = 1;
		this.tiempoVida = poblacionParams.get("TiempoVida");
		this.posInicial = posInicial;
		this.contexto = contexto;
		generarPrimeraGen();
	}
	
	private void generarPrimeraGen() {
		for (int i=0; i < entidades.length; i++) {
			entidades[i] = new Entidad(this, null);
		}
	
	}

	public void realizarCiclo() {
		for(int i=0; i < entidades.length; i++) {
			entidades[i].actuar();
			contexto.getControlador().mostrarEntidad(entidades[i]);
		}
	}
	
	public void seleccionar() {
		poolGenetico.clear();
		double mejorAptitud = evaluarEntidades();
		for(Entidad entidad : entidades) {
			entidad.setAptitud(entidad.getAptitud() / mejorAptitud);
		}
		deterProbabilidad();
	}

	private double evaluarEntidades() {
		double mejorAptitud = 0.0;
		for(Entidad entidad : entidades) {
			if (entidad.evaluarAptitud() > mejorAptitud) {
				mejorAptitud = entidad.getAptitud();
				System.out.println("Distancia minima: " + entidad.getDistanciaMinima());
				System.out.println("Tiempo obtenido: " + entidad.getTiempoObtenido());
			}
		}
		return mejorAptitud;
	}

	private void deterProbabilidad() {
		for(Entidad entidad : entidades) {
			int probabilidad = (int) (entidad.getAptitud() * 100);
			for(int i=0; i < probabilidad; i++) {
				poolGenetico.add(entidad);
			}
		}
	}

	
	public void reproducir() {
		Entidad[] nuevaGeneracion = new Entidad[entidades.length];
		for(int i=0; i < entidades.length; i++) {
			int randomInd1 = rng.nextInt(poolGenetico.size());
			Entidad pariente1 = poolGenetico.get(randomInd1);
			int randomInd2 = 0; 
			do {
				randomInd2 = rng.nextInt(poolGenetico.size());
			} while(poolGenetico.get(randomInd2).getAptitud() == pariente1.getAptitud());
			Entidad pariente2 = poolGenetico.get(randomInd2);
			ADN adnHijo = cruzarEntidades(pariente1, pariente2);
			mutar(adnHijo);
			nuevaGeneracion[i] = new Entidad(this, adnHijo);
		}
		entidades = nuevaGeneracion;
		numGeneraciones++;
	}
	
	private ADN cruzarEntidades(Entidad pariente1, Entidad pariente2) {
		PVector[] genesHijo = new PVector[getTiempoVida()];
		PVector[] genesPariente1 = pariente1.getAdn().getGenes();
	    PVector[] genesPariente2 = pariente2.getAdn().getGenes();
		for(int i=0; i < genesHijo.length; i++) {
			if (rng.nextBoolean()) {
				genesHijo[i] = genesPariente1[i];
			} else {
				genesHijo[i] = genesPariente2[i];
			}
		}
		return new ADN(genesHijo);
	}
	
//	private ADN cruzarEntidades(Entidad pariente1, Entidad pariente2) {
//		PVector[] genesHijo = new PVector[getTiempoVida()];
//		int puntoMedio = rng.nextInt(genesHijo.length);
//		for(int i=0; i < genesHijo.length; i++) {
//			if (i < puntoMedio) {
//				genesHijo[i] = pariente1.getAdn().getGenes()[i];
//			} else {
//				genesHijo[i] = pariente2.getAdn().getGenes()[i];
//			}
//		}
//		return new ADN(genesHijo);
//	}
	
	private void mutar(ADN adnHijo) {
		for(PVector gen : adnHijo.getGenes()) {
			if(rng.nextDouble(1) < tasaMutacion) {
				adnHijo.generarFuerzaAleatoria(gen);
			}
		}
	}
	
	public Entidad[] getEntidades() {
		return entidades;
	}

	public void setTasaMutacion(double tasaMutacion) {
		this.tasaMutacion = tasaMutacion;
	}

	public int getNumGeneraciones() {
		return numGeneraciones;
	}

	public PVector getPosInicial() {
		return posInicial;
	}

	public void setPosInicial(PVector posInicial) {
		this.posInicial = posInicial;
	}

	public int getTiempoVida() {
		return tiempoVida;
	}

	public void setTiempoVida(int tiempoVida) {
		this.tiempoVida = tiempoVida;
	}

	public Modelo getContexto() {
		return contexto;
	}
	
}
