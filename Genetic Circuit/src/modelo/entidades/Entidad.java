package modelo.entidades;

import modelo.Meta;
import processing.core.PVector;

public class Entidad {
	
	private ADN adn;
	private int genActual;
	
	private PVector posicion, velocidad, aceleracion;
	
	private boolean haChocado, haLlegado;
	
	private double aptitud;
	private double distanciaMinima;
	private int tiempoObtenido;
	
	public Entidad(int tiempoVida, PVector posInicial) {
		adn = new ADN(tiempoVida);
		posicion = posInicial;
		velocidad = new PVector(0,0);
		aceleracion = new PVector(0,0);
		aptitud = genActual = 0;
		haChocado = haLlegado = false;
		comprobarObjetivo();
	}
	
	public void actuar() {
		if (!haChocado && !haLlegado) {
			PVector fuerzaGenética = adn.getGenes()[genActual];
			moverEntidad(fuerzaGenética);
			comprobarColisiones();
			comprobarObjetivo();
		}
		
	}

	private void moverEntidad(PVector fuerza) {
		aceleracion.add(fuerza);
		velocidad.add(aceleracion);
		posicion.add(velocidad);
		aceleracion.mult(0);
	}
	
	//TODO Crear coleccion de obstaculos para comprobar si se ha chocado con alguno
	private void comprobarColisiones() {
//		for (Obstaculo obstaculo : obstaculos) {
//			if(obstaculo.chocaConEntidad(posicion)) {
//				haChocado = true;
//			}
//		}
	}
	
	private void comprobarObjetivo() {
		double distancia = PVector.dist(posicion, Meta.getPosicion());
		if(distancia < distanciaMinima) {
			distanciaMinima = distancia;
		}
		if(Meta.chocaConEntidad(posicion)) {
			haLlegado = true;
		} else {
			tiempoObtenido++;
		}
	}
	
	public void evaluarAptitud() {
		aptitud = Math.pow(1 / (tiempoObtenido * distanciaMinima), 2);
		if(haChocado) {
			aptitud *= 0.1;
		}
		if (haLlegado) {
			aptitud *= 2;
		}
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
	
}
