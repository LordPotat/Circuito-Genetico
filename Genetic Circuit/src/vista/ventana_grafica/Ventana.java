package vista.ventana_grafica;

import controlador.Controlador;
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
	}
	
	public void draw() {
		background(255);
		drawFramerate();
		drawMeta(new PVector(this.width - 50f, 50f), 50f, 50f);
		Poblacion poblacionEntidades = controlador.getModelo().getPoblacionEntidades();
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
	
	public void drawEntidades() {
		pushMatrix();
		
		popMatrix();
	}
	
	public void drawObstaculos() {
		
	}
	
	private void drawFramerate() {
		fill(125);
		textSize(16);
		textAlign(0, CENTER);
		text("Framerate: " + round(frameRate), 10, 10);
	}
}
