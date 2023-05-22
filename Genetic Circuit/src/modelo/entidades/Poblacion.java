package modelo.entidades;

import java.util.ArrayList;

public class Poblacion {
	
	private Entidad[] entidades;
	private ArrayList<Entidad> poolGenetico;
	private double tasaMutacion;
	private int numGeneraciones;
	
	public Poblacion(int numEntidades, double tasaMutacion) {
		entidades = new Entidad[numEntidades];
		this.tasaMutacion = tasaMutacion;
	}
	
	public void realizarCiclo() {
		for(int i=0; i < entidades.length; i++) {
			entidades[i].actuar();
		}
	}
	
	public void seleccionar() {
		
	}
	
	public void reproducir() {
		
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
	
}
