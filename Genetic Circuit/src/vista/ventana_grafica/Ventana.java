package vista.ventana_grafica;

import controlador.Controlador;
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
		Modelo modelo = controlador.getModelo();
		drawMeta(modelo.getMeta().getPosicion(), modelo.getMeta().getAncho(), modelo.getMeta().getAlto());
		drawObstaculos(modelo.getObstaculos());
		Poblacion poblacionEntidades = modelo.getPoblacionEntidades();
		if(numFramesGen < poblacionEntidades.getTiempoVida()) {
			poblacionEntidades.realizarCiclo();
			numFramesGen++;
		} else {
			numFramesGen = 0;
			poblacionEntidades.seleccionar();
			poblacionEntidades.reproducir();
		}
	}
	
	public void drawMeta(PVector posicion, float ancho, float alto) {
		pushMatrix();
		translate(posicion.x, posicion.y);
		fill(0, 255, 0);
		ellipseMode(CENTER);
		ellipse(0, 0, ancho, alto);
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
	
	public void drawObstaculos(Obstaculo[] obstaculos) {
		for(Obstaculo obs: obstaculos) {
			drawObstaculo(obs.getPosicion(), obs.getAncho(), obs.getAlto(), obs.getAngulo());
		}
	}
	
	private void drawObstaculo(PVector posicion, float ancho, float alto, float angulo) {
		pushMatrix();
		stroke(0);
		fill(165);
		translate(posicion.x, posicion.y);
		rotate(angulo);
		rectMode(CENTER);
		rect(0, 0, ancho, alto);
		popMatrix();
	}
	
	private void drawFramerate() {
		fill(125);
		textSize(16);
		textAlign(0, CENTER);
		text("Framerate: " + round(frameRate), 10, 10);
	}
}
