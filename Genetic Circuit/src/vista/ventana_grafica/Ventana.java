package vista.ventana_grafica;

import processing.core.PApplet;
import processing.core.PImage;

public class Ventana extends PApplet {

	public static Ventana crearVentana(String[] processingArgs) {
		Ventana ventana = new Ventana();
		PApplet.runSketch(processingArgs, ventana);
		return ventana;
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
		background(255);
	}
	
	public void draw() {
		background(255);
		drawFramerate();
	}

	private void drawFramerate() {
		fill(125);
		textSize(16);
		textAlign(0, CENTER);
		text("Framerate: " + round(frameRate), 10, 10);
	}
}
