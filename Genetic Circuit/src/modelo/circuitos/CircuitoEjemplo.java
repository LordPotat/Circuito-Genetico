package modelo.circuitos;

import java.util.HashMap;

import processing.core.PVector;
import vista.ventana_grafica.Ventana;
import java.util.ArrayList;

public class CircuitoEjemplo {
	
	private int tiempoObjetivo = 100;
	
	public PVector setSpawn(Ventana ventana)  {
		return new PVector(ventana.width/2, ventana.height-30);
	}
	
	public HashMap<String, Object> setupMeta(Ventana ventana) {
		HashMap<String, Object> metaParams = new HashMap<String, Object>();
		metaParams.put("Posicion", new PVector(ventana.width/2, 40));
		metaParams.put("Ancho", 50f);	
		metaParams.put("Alto", 50f);
		return metaParams;
	}
	
	public ArrayList<HashMap<String, Object>> setupObstaculos(Ventana ventana) {
		ArrayList<HashMap<String, Object>> obsParams = new ArrayList<HashMap<String, Object>>();
		int numObstaculos = 9;
		for(int i=0; i < numObstaculos; i++) {
			obsParams.add(new HashMap<String, Object>());
		}
		obsParams.get(0).put("Posicion", new PVector(ventana.width/2, ventana.height/2));
		obsParams.get(0).put("Ancho", 200f);	
		obsParams.get(0).put("Alto", 50f);
		obsParams.get(0).put("Angulo", 0f);
		obsParams.get(1).put("Posicion", new PVector(ventana.width/2 - 200, ventana.height/2 + 200));
		obsParams.get(1).put("Ancho", 200f);	
		obsParams.get(1).put("Alto", 50f);
		obsParams.get(1).put("Angulo", 0f);
		obsParams.get(2).put("Posicion", new PVector(ventana.width/2 + 200, ventana.height/2 + 200));
		obsParams.get(2).put("Ancho", 200f);	
		obsParams.get(2).put("Alto", 50f);
		obsParams.get(2).put("Angulo", 0f);
		obsParams.get(3).put("Posicion", new PVector(ventana.width/2 - 200, ventana.height/2 - 200));
		obsParams.get(3).put("Ancho", 200f);	
		obsParams.get(3).put("Alto", 50f);
		obsParams.get(3).put("Angulo", 0f);
		obsParams.get(4).put("Posicion", new PVector(ventana.width/2 + 200, ventana.height/2 - 200));
		obsParams.get(4).put("Ancho", 200f);	
		obsParams.get(4).put("Alto", 50f);
		obsParams.get(4).put("Angulo", 0f);
		obsParams.get(5).put("Posicion", new PVector(ventana.width/2 - 300, ventana.height/2));
		obsParams.get(5).put("Ancho", 50f);	
		obsParams.get(5).put("Alto", (float)ventana.height);
		obsParams.get(5).put("Angulo", 0f);
		obsParams.get(6).put("Posicion", new PVector(ventana.width/2 + 300, ventana.height/2));
		obsParams.get(6).put("Ancho", 50f);	
		obsParams.get(6).put("Alto", (float)ventana.height);
		obsParams.get(6).put("Angulo", 0f);
		obsParams.get(7).put("Posicion", new PVector(ventana.width/2, ventana.height + 5));
		obsParams.get(7).put("Ancho", (float)ventana.width);	
		obsParams.get(7).put("Alto", 20f);
		obsParams.get(7).put("Angulo", 0f);
		obsParams.get(8).put("Posicion", new PVector(ventana.width/2, 0));
		obsParams.get(8).put("Ancho", (float)ventana.width);	
		obsParams.get(8).put("Alto", 10f);
		obsParams.get(8).put("Angulo", 0f);
		return obsParams;
	}

	public int getTiempoObjetivo() {
		return tiempoObjetivo;
	}
	
}
