package vista.ventana_grafica;

import controlador.Controlador;
import modelo.Meta;
import modelo.Modelo;
import modelo.Obstaculo;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PJOGL;

public class Ventana extends PApplet {

	private static Ventana instancia = null;
	
	private Controlador controlador;
	private int numFramesGen;
	private PShape humanoide;
	private boolean modoDebug;
	
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
		size(1280, 720, P2D);
		PJOGL.setIcon("gene_icon.png");
		smooth(16);
	}
	
	public void setup() {
		windowTitle("Circuito Genético");
//		GraphicsDevice pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
//		int tasaRefresco = pantalla.getDisplayMode().getRefreshRate();
		frameRate(60);
		stroke(0);
		background(255);
		humanoide = crearFormaEntidad();
		numFramesGen = 0;
		modoDebug = false;
	}
	
	public void draw() {
		background(255);
		stroke(0);
		drawFramerate();
		drawCircuito();
		int numGeneraciones = controlador.manipularPoblacion();
		drawGeneraciones(numGeneraciones);
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
        strokeWeight(4);
        ellipseMode(CENTER);
        ellipse(0, 0, meta.getAncho(), meta.getAlto());
        popMatrix();
        strokeWeight(1);
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
		shape(humanoide);
		if(modoDebug == true) {
			drawFlechaDireccion(velocidad);
		}
		popMatrix();
	}
	
	private void drawFlechaDireccion(PVector velocidad) {
		float magnitudVelocidad = velocidad.mag() * 5;
		stroke(255,0,0);
		line(0, 0, magnitudVelocidad, 0);
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, -5);
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, 5);
		stroke(0);
	}

	private PShape crearFormaEntidad() {
		
		PShape humanoide = createShape(GROUP);
		
		PShape cuerpo = createShape(TRIANGLE, -25, -10, 0, 0, -25, 10);
		cuerpo.setFill(color(0, 0, 255)); 
	    cuerpo.setStroke(color(0));
	    cuerpo.setStrokeWeight(2);
		humanoide.addChild(cuerpo);

	    PShape cabeza = createShape(ELLIPSE, 0, 0, 10, 10);
	    cabeza.setFill(color(0, 0, 255)); 
	    cabeza.setStroke(color(0));
	    cabeza.setStrokeWeight(2);
	    humanoide.addChild(cabeza);

	    PShape extremidades = createShape();
	    extremidades.beginShape(LINES);
	    extremidades.stroke(0,0,255);
	    extremidades.vertex(-10, -5);
	    extremidades.vertex(5, -10);
	    extremidades.vertex(-10, 5);
	    extremidades.vertex(5, 10);
	    extremidades.vertex(-25, -5);
	    extremidades.vertex(-40, -5);
	    extremidades.vertex(-25, 5);
	    extremidades.vertex(-40, 5);
	    extremidades.endShape();
	    humanoide.addChild(extremidades);
		
	    return humanoide;
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
	
	public void drawGeneraciones(int numGeneraciones) {
		pushMatrix();
		fill(125);
		textSize(16);
		textAlign(LEFT, CENTER);
		text("Generaciones: " + numGeneraciones, 10, 31);
		popMatrix();
	}

	public void keyPressed() {
		if(key == 'd') {
			modoDebug = !modoDebug;
		}
	}
	
	public int getNumFramesGen() {
		return numFramesGen;
	}

	public void setNumFramesGen(int numFramesGen) {
		this.numFramesGen = numFramesGen;
	}
	
}
