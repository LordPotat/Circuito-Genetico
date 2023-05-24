package modelo.circuitos;

import java.util.HashMap;

import processing.core.PVector;
import vista.ventana_grafica.Ventana;
import java.util.ArrayList;

public class CircuitoEjemplo {
	
	private static int tiempoObjetivo = 250;
	
	public static PVector setSpawn(Ventana ventana)  {
		return new PVector(ventana.width/2, ventana.height-10);
	}
	
	public static HashMap<String, Object> setupMeta(Ventana ventana) {
		HashMap<String, Object> metaParams = new HashMap<String, Object>();
		metaParams.put("Posicion", new PVector(ventana.width/2, 30));
		metaParams.put("Ancho", 50f);	
		metaParams.put("Alto", 50f);
		return metaParams;
	}
	
	public static ArrayList<HashMap<String, Object>> setupObstaculos(Ventana ventana) {
		ArrayList<HashMap<String, Object>> obsParams = new ArrayList<HashMap<String, Object>>();
		int numObstaculos = 1;
		for(int i=0; i < numObstaculos; i++) {
			obsParams.add(new HashMap<String, Object>());
		}
		obsParams.get(0).put("Posicion", new PVector(ventana.width/2, ventana.height/2));
		obsParams.get(0).put("Ancho", 200f);	
		obsParams.get(0).put("Alto", 50f);
		obsParams.get(0).put("Angulo", 0f);
		return obsParams;
	}

	public static int getTiempoObjetivo() {
		return tiempoObjetivo;
	}
	
}
