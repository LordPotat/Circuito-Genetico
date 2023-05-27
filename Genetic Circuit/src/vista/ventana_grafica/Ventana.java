package vista.ventana_grafica;

import controlador.Controlador;
import modelo.Meta;
import modelo.Modelo;
import modelo.Obstaculo;
import modelo.entidades.Poblacion;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Ventana extends PApplet {

	private static Ventana instancia = null;
	
	private Controlador controlador;
	private int numFramesGen;
	
	private Ventana(Controlador controlador) {
		this.controlador = controlador;
	}
	
	public static Ventana crearVentana(String[] processingArgs, Controlador controlador) {
		if (instancia == null) {
			instancia = new Ventana(controlador);
		}
		PApplet.runSketch(processingArgs, instancia);
		return instancia;
	}
	
	public void settings() {
		size(1280, 720);
		smooth();
	}
	
	public void setup() {
		PImage icono = loadImage("gene_icon.png");
		surface.setIcon(icono);
		windowTitle("Circuito Genético");
		frameRate(60);
		stroke(0);
		background(255);
		numFramesGen = 0;
	}
	
	public void draw() {
		background(255);
		stroke(0);
		drawFramerate();
		drawCircuito();
		manipularPoblacion();
	}

	private void manipularPoblacion() {
		Poblacion entidades = controlador.getModelo().getPoblacion();
		drawGeneraciones(entidades.getNumGeneraciones());
		if(entidades.isObjetivoCumplido()) {
			controlador.mostrarRutaOptima(entidades.getMejorEntidad());
			return;
		}
		if(numFramesGen < entidades.getTiempoVida()) {
			entidades.realizarCiclo();
			numFramesGen++;
		} else {
			numFramesGen = 0;
			entidades.evolucionar();
		}
	}

	private void drawCircuito() {
		Modelo modelo = controlador.getModelo();
		drawMeta(modelo.getMeta());
		drawObstaculos(modelo.getObstaculos());
	}
	
	public void drawMeta(Meta meta) {
		pushMatrix();
		translate(meta.getPosicion().x, meta.getPosicion().y);
        fill(0, 255, 0);
        ellipseMode(CENTER);
        ellipse(0, 0, meta.getAncho(), meta.getAlto());
        popMatrix();
	}
	
	public void drawObstaculos(Obstaculo[] obstaculos) {
		for(Obstaculo obs: obstaculos) {
			drawObstaculo(obs);
		}
	}
	
	private void drawObstaculo(Obstaculo obstaculo) {
		pushMatrix();
        stroke(0);
        fill(165);
        translate(obstaculo.getPosicion().x, obstaculo.getPosicion().y);
        rotate(obstaculo.getAngulo());
        rectMode(CENTER);
        rect(0, 0, obstaculo.getAncho(), obstaculo.getAlto());
        popMatrix();
	}
	
	public void drawEntidad(PVector posicion, PVector velocidad) {
		pushMatrix();
		translate(posicion.x, posicion.y);
		rotate(atan2(velocidad.y, velocidad.x));
		drawHumanoide();
		drawFlechaDireccion(velocidad);
		popMatrix();
	}

	private void drawHumanoide() {
		stroke(0);
		fill(0, 0, 255);
		triangle(-25, -10, 0, 0, -25, 10);
		ellipseMode(CENTER);
		ellipse(0, 0, 10, 10);
		line(-10, -5, 5, -10);
		line(-10, 5, 5, 10);
		line(-25, -5, -40, -5);
		line(-25, 5, -40, 5);
	}
	
	private void drawFlechaDireccion(PVector velocidad) {
		stroke(255, 0, 0);
		float magVector = velocidad.mag() * 5;
		line(0, 0, magVector, 0);
		line(magVector, 0, magVector - 5, -5);
		line(magVector, 0, magVector - 5, 5);
		stroke(0);
	}
	
	public void drawRutaOptima(PVector[] ruta, int tiempoObtenido) {
		stroke(255,0,0);
		strokeWeight(5);
		PVector posicion = controlador.getModelo().getPoblacion().getPosInicial().copy();
		ellipseMode(CENTER);
		fill(255);
		ellipse(posicion.x, posicion.y, 20, 20);
		PVector velocidad = new PVector(0,0);
		PVector aceleracion = new PVector(0,0);
		for(int i = 0; i < tiempoObtenido - 1; i++) {
			drawLineaRuta(ruta[i], posicion, velocidad, aceleracion);
		}
		strokeWeight(1);
	}

	private void drawLineaRuta(PVector fuerza, PVector posicion, PVector velocidad, PVector aceleracion) {
		PVector posicionPrevia = posicion.copy();
		aceleracion.add(fuerza);
		velocidad.add(aceleracion);
		posicion.add(velocidad);
		line(posicionPrevia.x, posicionPrevia.y, posicion.x, posicion.y);
		aceleracion.mult(0);
	}
	
	private void drawFramerate() {
		fill(125);
		textSize(16);
		textAlign(LEFT, CENTER);
		text("Framerate: " + round(frameRate), 10, 15);
	}
	
	private void drawGeneraciones(int numGeneraciones) {
		fill(125);
		textSize(16);
		textAlign(LEFT, CENTER);
		text("Generaciones: " + numGeneraciones, 10, 31);
	}
}
