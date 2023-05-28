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
	private int mejorTiempo;
	private boolean objetivoCumplido;
	private Entidad mejorEntidad;
	private Random random = new Random();
	
	public Poblacion(Modelo contexto, HashMap<String, Integer> poblacionParams, PVector posInicial) {
		this.contexto = contexto;
		entidades = new Entidad[poblacionParams.get("NumEntidades")];
		poolGenetico = new ArrayList<Entidad>();
		this.tasaMutacion = ((double) poblacionParams.get("TasaMutacion")) / 100;
		numGeneraciones = 1;
		this.tiempoVida = poblacionParams.get("TiempoVida");
		this.posInicial = posInicial;
		mejorTiempo = tiempoVida;
		objetivoCumplido = false;
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
	
	public void evolucionar() {
		seleccionar();
		if(!objetivoCumplido) {
			reproducir();
		} else {
			contexto.getControlador().mostrarRutaOptima(mejorEntidad);
		}
		
	}
	
	private void seleccionar() {
		poolGenetico.clear();
		double mejorAptitud = evaluarEntidades();
		if(comprobarObjetivo()) {
			objetivoCumplido = true;
			return;
		}
		for(Entidad entidad : entidades) {
			entidad.setAptitud(entidad.getAptitud() / mejorAptitud);
		}
		calcProbabilidadReproduccion();
	}

	private boolean comprobarObjetivo() {
		return mejorTiempo <= contexto.getCircuito().getTiempoObjetivo();
	}

	private double evaluarEntidades() {
		double mejorAptitud = 0.0;
		for(Entidad entidad : entidades) {
			if (entidad.evaluarAptitud() > mejorAptitud) {
				mejorAptitud = entidad.getAptitud();
//				System.out.println("Tiempo obtenido: " + entidad.getTiempoObtenido());
				comprobarTiempoRecord(entidad);
			}
		}
		return mejorAptitud;
	}

	private void comprobarTiempoRecord(Entidad entidad) {
		if(entidad.getTiempoObtenido() < mejorTiempo) {
			mejorTiempo = entidad.getTiempoObtenido();
			mejorEntidad = entidad;
			System.out.println("Nuevo mejor tiempo: " + mejorTiempo);
		}
	}

	private void calcProbabilidadReproduccion() {
		for(Entidad entidad : entidades) {
			int probabilidad = (int) (entidad.getAptitud() * 100);
			for(int i=0; i < probabilidad; i++) {
				poolGenetico.add(entidad);
			}
		}
	}

	private void reproducir() {
		Entidad[] nuevaGeneracion = new Entidad[entidades.length];
		for(int i=0; i < entidades.length; i++) {
			int indPariente1 = random.nextInt(poolGenetico.size());
			Entidad pariente1 = poolGenetico.get(indPariente1);
			Entidad pariente2 = encontrarParienteDistinto(pariente1);
			ADN adnHijo = cruzarEntidades(pariente1, pariente2);
			mutar(adnHijo);
			nuevaGeneracion[i] = new Entidad(this, adnHijo);
		}
		entidades = nuevaGeneracion;
		numGeneraciones++;
	}

	private Entidad encontrarParienteDistinto(Entidad pariente1) {
		int indPariente2;
		do {
			indPariente2 = random.nextInt(poolGenetico.size());
		} while(poolGenetico.get(indPariente2).getAptitud() == pariente1.getAptitud());
		Entidad pariente2 = poolGenetico.get(indPariente2);
		return pariente2;
	}
	
	private ADN cruzarEntidades(Entidad pariente1, Entidad pariente2) {
		PVector[] genesHijo = new PVector[getTiempoVida()];
		PVector[] genesPariente1 = pariente1.getAdn().getGenes();
	    PVector[] genesPariente2 = pariente2.getAdn().getGenes();
		for(int i=0; i < genesHijo.length; i++) {
			if (random.nextBoolean()) {
				genesHijo[i] = genesPariente1[i];
			} else {
				genesHijo[i] = genesPariente2[i];
			}
		}
		return new ADN(genesHijo);
	}
	
	private void mutar(ADN adnHijo) {
		for(PVector gen : adnHijo.getGenes()) {
			if(random.nextDouble(1) < tasaMutacion) {
				adnHijo.generarGenAleatorio(gen);
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

	public boolean isObjetivoCumplido() {
		return objetivoCumplido;
	}

	public Entidad getMejorEntidad() {
		return mejorEntidad;
	}
	
}
