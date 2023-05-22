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
	
//	public Poblacion(Modelo contexto, int numEntidades, double tasaMutacion, int tiempoVida, PVector posInicial) {
//		entidades = new Entidad[numEntidades];
//		poolGenetico = new ArrayList<Entidad>();
//		this.tasaMutacion = tasaMutacion;
//		numGeneraciones = 1;
//		this.tiempoVida = tiempoVida;
//		this.posInicial = posInicial;
//		this.contexto = contexto;
//	}
	
	public Poblacion(Modelo contexto, HashMap<String, Integer> poblacionParams, PVector posInicial) {
		entidades = new Entidad[poblacionParams.get("NumEntidades")];
		poolGenetico = new ArrayList<Entidad>();
		this.tasaMutacion = ((double) poblacionParams.get("TasaMutacion")) / 100;
		numGeneraciones = 1;
		this.tiempoVida = poblacionParams.get("TiempoVida");
		this.posInicial = posInicial;
		this.contexto = contexto;
	}
	
	public void realizarCiclo() {
		for(int i=0; i < entidades.length; i++) {
			entidades[i].actuar();
		}
	}
	
	public void seleccionar() {
		for (Entidad e: entidades) {
			e.evaluarAptitud();
			int numApariciones = (int) Math.round(e.getAptitud() * 100);
			for(int j=0; j < numApariciones; j++) {
				poolGenetico.add(e);
			}
		}
	}
	
	public void reproducir() {
		Random rng = new Random();
		for(int i=0; i < entidades.length; i++) {
			Entidad pariente1 = poolGenetico.get(rng.nextInt(poolGenetico.size()));
			Entidad pariente2 = poolGenetico.get(rng.nextInt(poolGenetico.size()));
			Entidad hijo = cruzarEntidades(pariente1, pariente2);
			mutar(hijo);
			entidades[i] = hijo;
		}
		poolGenetico.clear();
		numGeneraciones++;
	}
	
	private Entidad cruzarEntidades(Entidad pariente1, Entidad pariente2) {
		Random rng = new Random();
		Entidad hijo = new Entidad(this);
		PVector[] genesHijo = hijo.getAdn().getGenes();
		int puntoMedio = rng.nextInt(genesHijo.length);
		for(int i=0; i < genesHijo.length; i++) {
			if (i < puntoMedio) {
				genesHijo[i] = pariente1.getAdn().getGenes()[i];
			} else {
				genesHijo[i] = pariente2.getAdn().getGenes()[i];
			}
		}
		return hijo;
	}
	
	private void mutar(Entidad hijo) {
		Random rng = new Random();
		ADN adnHijo = hijo.getAdn();
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