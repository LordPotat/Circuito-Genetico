package modelo.entidades;

import modelo.Meta;
import modelo.Obstaculo;
import processing.core.PVector;
import vista.ventana_grafica.Ventana;

public class Entidad {
	
	private ADN adn;
	private int genActual;
	private Poblacion poblacion;
	
	private PVector posicion, velocidad, aceleracion;
	
	private boolean haChocado, haLlegado;
	
	private double aptitud;
	private double probReproduccion;
	private double probAcumulada;
	private double distanciaMinima;
	private int tiempoObtenido;
	
	public Entidad(Poblacion poblacion, ADN adn) {
		
		this.poblacion = poblacion;
		posicion = poblacion.getPosInicial().copy();
		velocidad = new PVector(0,0);
		aceleracion = new PVector(0,0);
		aptitud = genActual = 0;
		haChocado = haLlegado = false;
		distanciaMinima = PVector.dist(posicion, poblacion.getContexto().getMeta().getPosicion());
		if(adn != null) {
			this.adn = adn;
		} else {
			this.adn = new ADN(poblacion.getTiempoVida());
		}
	}
	
	public void actuar() {
		if (!haChocado && !haLlegado) {
			PVector fuerzaGenetica = adn.getGenes()[genActual];
			genActual++;
			moverEntidad(fuerzaGenetica);
			comprobarObjetivo();
			comprobarColisiones();
		} else if(haChocado) {
			tiempoObtenido++;
		}
	}

	private void moverEntidad(PVector fuerza) {
		aceleracion.add(fuerza);
		velocidad.add(aceleracion);
		posicion.add(velocidad);
		aceleracion.mult(0);
	}
	
	private void comprobarColisiones() {
		for (Obstaculo obstaculo : poblacion.getContexto().getObstaculos()) {
			if(obstaculo.chocaConEntidad(posicion)) {
				haChocado = true;
			}
		}
	}
	
	private void comprobarObjetivo() {
		Meta meta = poblacion.getContexto().getMeta();
		double distancia = PVector.dist(posicion, meta.getPosicion());
		if(distancia < distanciaMinima) {
			distanciaMinima = distancia;
		}
		if(meta.contieneEntidad(posicion)) {
			haLlegado = true;
		} else {
			tiempoObtenido++;
		}
		
	}
	
	public double evaluarAptitud() {
		float radioMeta = poblacion.getContexto().getMeta().getAlto() / 2;
		if (distanciaMinima < radioMeta) {
			distanciaMinima = radioMeta;
		}
		double factorTiempo = 1.0;
	    factorTiempo = (double)poblacion.getContexto().getCircuito().getTiempoObjetivo() / (double)tiempoObtenido;
		aptitud = Math.pow(factorTiempo,1/100) / (tiempoObtenido * distanciaMinima);
		if (haLlegado) {
			aptitud *= 2;
		}
		return aptitud;
	}

	
	
	public ADN getAdn() {
		return adn;
	}

	public double getAptitud() {
		return aptitud;
	}

	public PVector getPosicion() {
		return posicion;
	}

	public PVector getVelocidad() {
		return velocidad;
	}

	public PVector getAceleracion() {
		return aceleracion;
	}

	public double getDistanciaMinima() {
		return distanciaMinima;
	}

	public double getProbReproduccion() {
		return probReproduccion;
	}

	public void setProbReproduccion(double probReproduccion) {
		this.probReproduccion = probReproduccion;
	}

	public double getProbAcumulada() {
		return probAcumulada;
	}

	public void setProbAcumulada(double probAcumulada) {
		this.probAcumulada = probAcumulada;
	}

	public void setAptitud(double aptitud) {
		this.aptitud = aptitud;
	}

	public int getTiempoObtenido() {
		return tiempoObtenido;
	}
	
	
}
