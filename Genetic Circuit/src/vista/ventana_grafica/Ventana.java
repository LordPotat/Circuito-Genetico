package vista.ventana_grafica;

import controlador.Controlador;
import controlador.Estado;
import modelo.Modelo;
import modelo.circuito.Meta;
import modelo.circuito.Obstaculo;
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
	
	public static Ventana crearVentana(Controlador controlador) {
		if (instancia == null) {
			instancia = new Ventana(controlador);
		}
		return instancia;
	}
	
	public void settings() {
		size(1280, 720, P2D);
		PJOGL.setIcon("gene_icon.png");
		smooth(16);
	}
	
	public void setup() {
		windowTitle("Circuito Genético");
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
		drawEstado(controlador.getEstado());
		drawCircuito();
		if(!controlador.isParado()) {
			controlador.manipularPoblacion();
		} else if (controlador.getEstado() == Estado.PAUSADO) {
			controlador.mostrarEntidadesActivas();
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
	
	public void drawEntidad(PVector posicion, PVector velocidad, boolean monitorizada) {
		pushMatrix();
		translate(posicion.x, posicion.y);
		rotate(atan2(velocidad.y, velocidad.x));
		int colorStroke = monitorizada ? color(204, 0, 255) : color(0, 0, 0);
		humanoide.setStroke(colorStroke);
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
		strokeWeight(3);
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
		PVector posicionPrevia = simularMovimiento(fuerza, posicion, velocidad, aceleracion);
		line(posicionPrevia.x, posicionPrevia.y, posicion.x, posicion.y);
		pushMatrix();
		drawFlechaDireccion(velocidad, posicionPrevia);
		popMatrix();
		stroke(255,0,0);
		aceleracion.mult(0);
	}

	private void drawFlechaDireccion(PVector velocidad, PVector posicionPrevia) {
		translate(posicionPrevia.x, posicionPrevia.y);
		rotate(atan2(velocidad.y, velocidad.x));
		stroke(204, 0, 255);
		float magnitudVelocidad = velocidad.mag() * 5;
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, -5);
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, 5);
	}

	private PVector simularMovimiento(PVector fuerza, PVector posicion, PVector velocidad, PVector aceleracion) {
		PVector posicionPrevia = posicion.copy();
		aceleracion.add(fuerza);
		velocidad.add(aceleracion);
		posicion.add(velocidad);
		return posicionPrevia;
	}
	
	private void drawFramerate() {
		fill(80);
		textSize(16);
		textAlign(LEFT, CENTER);
		text("Framerate: " + round(frameRate), 10, 15);
	}
	
	public void drawEstado(Estado estado) {
		textSize(18);
		textAlign(LEFT, CENTER);
		fill(80); 
		text("Estado: ", 9, 45);
		int[] colorEstado = estado.getColor();
		fill(colorEstado[0], colorEstado[1], colorEstado[2]); 
		text(estado.getTexto(), 9 + textWidth("Estado: "), 45);
	}

	public void mousePressed() {
		controlador.seleccionarEntidad(new PVector(mouseX, mouseY));
	}

	public void keyPressed() {
		if(key == ' ') {
			controlador.getControladorEventos().realizarEventoEspacio();
			return;
		}
		if(key == 'a' || key == 'A') {
			controlador.getControladorEventos().cambiarModo();
			return;
		}
		if(key == 'r' || key == 'R') {
			controlador.getControladorEventos().reiniciar();
			return;
		}
		if(key == 'd' || key == 'D') {
			modoDebug = !modoDebug;
			return;
		}
	}
	
	public int getNumFramesGen() {
		return numFramesGen;
	}

	public void setNumFramesGen(int numFramesGen) {
		this.numFramesGen = numFramesGen;
	}
	
}
