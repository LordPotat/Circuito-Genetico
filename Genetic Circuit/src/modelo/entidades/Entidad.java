package modelo.entidades;

import modelo.Meta;
import modelo.Obstaculo;
import processing.core.PApplet;
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
		posicion = poblacion.getPosInicial();
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
			PVector fuerzaGenética = adn.getGenes()[genActual];
			genActual++;
			moverEntidad(fuerzaGenética);
			comprobarObjetivo();
			//comprobarColisiones();
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
		if(meta.chocaConEntidad(posicion)) {
			haLlegado = true;
		} else {
			tiempoObtenido++;
		}
	}
	
	public double evaluarAptitud() {
		if (distanciaMinima < 1) {
			distanciaMinima = 1;
		}
		aptitud = Math.pow(1 / (tiempoObtenido * distanciaMinima), 2);
		if(haChocado) {
			aptitud *= 0.1;
		}
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
	
	
}
