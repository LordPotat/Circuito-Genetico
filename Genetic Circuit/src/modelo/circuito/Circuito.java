
package modelo.circuito;

import java.util.HashMap;

import processing.core.PVector;
import vista.ventana_grafica.Ventana;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Almacena todos los datos que determinan en qué posición se encontrarán los elementos
 * del sistema, así como que atributos tendrán para mostrarse e interactuar entre ellos
 * @author Alberto
 */
public class Circuito implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Punto de donde parten todas las entidades. Es un Point2D.Float en vez de un PVector
	 * porque PVector no permite la serialización, pero esta clase es lo suficientemente 
	 * similar para poder convertir los datos de los vectores con facilidad al cargar el 
	 * objeto 
	 */
	private Point2D.Float spawn;
	
	/**
	 * Mapa que contiene todos los parámetros que definen la meta
	 */
	private HashMap<String, Object> metaParams;
	
	/**
	 * Mapa que contiene todos los parámetros que definen los obstáculos
	 */
	private ArrayList<HashMap<String, Object>> obstaculosParams;
	
	/**
	 * Crea un circuito con unos parámetros predefinidos para cada elemento, y después almacena el
	 * objeto con todos los atributos en un fichero dentro de los archivos del programa
	 * Tras guardar el circuito, se podrá cargar (deserializando el objeto desde su fichero)
	 * @param nombreCircuito nombre que tendrá el fichero almacenado
	 * @param ventana para poder establecer posiciones relativas
	 */
	public static void guardarCircuito(String nombreCircuito, Ventana ventana) {
		//Crea un circuito vacío
		Circuito circuito = new Circuito();
		//Llama a los métodos para asignar todos los parámetros correspondientes
		circuito.asignarSpawn(ventana);
		circuito.crearMeta(ventana);
		circuito.crearObstaculos(ventana);
		//Le añade el nombre del circuito como el fichero en la ruta del fichero
		String rutaFichero = "res/circuits/" + nombreCircuito + ".cir";
		//Serializa el objeto Circuito en la ruta indicada
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaFichero))) {
            oos.writeObject(circuito);
            System.out.println("Se ha guardado el circuito: " + nombreCircuito);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Carga desde un fichero con el nombre indicado un objeto que deserializará
	 * para devolver un circuito con una serie de elementos ya definidos para que
	 * no tengamos que generarlo durante el programa 
	 * @param nombreCircuito nombre del fichero a cargar
	 * @return un objeto Circuito con todos los elementos ya establecidos
	 */
	public static Circuito cargarCircuito(String nombreCircuito) {
		Circuito circuito = null;
		//Obtiene la ruta del fichero a través del nombre del circuito que buscamos
		String rutaFichero = "res/circuits/" + nombreCircuito + ".cir";
		//Carga el objeto del fichero como un Circuito
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaFichero))) {
			circuito = (Circuito) ois.readObject();
			System.out.println("Se ha cargado el circuito: " + nombreCircuito);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return circuito;
	}

	/**
	 * Establece el punto de spawn (punto inicial) en el que aparecen las entidades
	 * @param ventana gráfica para determinar la posición relativa del spawn
	 * @return el vector de posición del spawn
	 */
	public PVector setSpawn()  {
		//Convierte el Point2D.Float (serializable) en un objeto PVector (no serializable)
		return new PVector(spawn.x, spawn.y); 
	}
	
	/**
	 * Establece los parámetros de inicialización de la meta
	 * @param ventana gráfica para determinar la posición relativa de la meta
	 * @return el mapa con los parámetros
	 */
	public HashMap<String, Object> setupMeta(Ventana ventana) {
		HashMap<String, Object> metaParams = new HashMap<String, Object>();
		//Convierte el Point2D.Float de la posición a un PVector
		Point2D.Float posicion = (Float) this.metaParams.get("Posicion");
		metaParams.put("Posicion", new PVector(posicion.x, posicion.y));
		//Asigna el resto de parámetros almacenados del fichero
		metaParams.put("Ancho", this.metaParams.get("Ancho"));	
		metaParams.put("Alto", this.metaParams.get("Alto"));
		return metaParams;
	}
	
	/**
	 * Establece los parámetros de inicialización de los obstáculos
	 * @param ventana gráfica para determinar la posición relativa de los obstáculos
	 * @return el mapa con los parámetros
	 */
	public ArrayList<HashMap<String, Object>> setupObstaculos(Ventana ventana) {
		//Copia todos los obstáculos del objeto del fichero en una nueva colección
		ArrayList<HashMap<String, Object>> obstaculosParams = new ArrayList<HashMap<String, Object>>(this.obstaculosParams);
		/* Por cada mapa de parámetros que definen un obstáculo, debe convertir el Point2D.Float
		 * de la posición a un PVector
		 */
		for(int i=0; i < obstaculosParams.size(); i++) {
			HashMap<String, Object> obstaculo = obstaculosParams.get(i);
			Point2D.Float posicion = (Float) obstaculo.get("Posicion");
			obstaculo.put("Posicion", new PVector(posicion.x, posicion.y));
		}
		return obstaculosParams;
	}
	
	/**
	 * Crea una posición de inicio para las entidades relativo a la ventana
	 * Para poder serializarlo, el vector se crea como Point2D.Float
	 * @param ventana
	 */
	private void asignarSpawn(Ventana ventana)  {
		spawn = new Point2D.Float(ventana.width/2, ventana.height-30);
	}
	
	/**
	 * Crea una meta que se mostrará en una posición relativa a la ventana
	 * @param ventana
	 */
	private void crearMeta(Ventana ventana) {
		metaParams = new HashMap<String, Object>();
		metaParams.put("Posicion", new Point2D.Float(ventana.width/2, 40));
		metaParams.put("Ancho", 50f);	
		metaParams.put("Alto", 50f);
	}
	
	/**
	 * Crea todos los obstáculos del circuito en posiciones relativas a la ventana
	 * y con un tamaño y rotación determinadas
	 * @param ventana
	 */
	private void crearObstaculos(Ventana ventana) {
		//Inicia una lista con todos los obstáculos que deberá tener el circuito
		obstaculosParams = new ArrayList<HashMap<String, Object>>();
		int numObstaculos = 9;
		for(int i=0; i < numObstaculos; i++) {
			obstaculosParams.add(new HashMap<String, Object>());
		}
		//Ajusta los parámetros para cada uno de los obstáculos individualmente
		obstaculosParams.get(0).put("Posicion", new Point2D.Float(ventana.width/2, ventana.height/2));
		obstaculosParams.get(0).put("Ancho", 200f);	
		obstaculosParams.get(0).put("Alto", 50f);
		obstaculosParams.get(0).put("Angulo", 0f);
		obstaculosParams.get(1).put("Posicion", new Point2D.Float(ventana.width/2 - 150, ventana.height/2 + 200));
		obstaculosParams.get(1).put("Ancho", 200f);	
		obstaculosParams.get(1).put("Alto", 50f);
		obstaculosParams.get(1).put("Angulo", -45f);
		obstaculosParams.get(2).put("Posicion", new Point2D.Float(ventana.width/2 + 150, ventana.height/2 + 200));
		obstaculosParams.get(2).put("Ancho", 200f);	
		obstaculosParams.get(2).put("Alto", 50f);
		obstaculosParams.get(2).put("Angulo", 45f);
		obstaculosParams.get(3).put("Posicion", new Point2D.Float(ventana.width/2 - 150, ventana.height/2 - 200));
		obstaculosParams.get(3).put("Ancho", 200f);	
		obstaculosParams.get(3).put("Alto", 50f);
		obstaculosParams.get(3).put("Angulo", 45f);
		obstaculosParams.get(4).put("Posicion", new Point2D.Float(ventana.width/2 + 150, ventana.height/2 - 200));
		obstaculosParams.get(4).put("Ancho", 200f);	
		obstaculosParams.get(4).put("Alto", 50f);
		obstaculosParams.get(4).put("Angulo", -45f);
		obstaculosParams.get(5).put("Posicion", new Point2D.Float(ventana.width/2 - 300, ventana.height/2));
		obstaculosParams.get(5).put("Ancho", 50f);	
		obstaculosParams.get(5).put("Alto", (float)ventana.height);
		obstaculosParams.get(5).put("Angulo", 0f);
		obstaculosParams.get(6).put("Posicion", new Point2D.Float(ventana.width/2 + 300, ventana.height/2));
		obstaculosParams.get(6).put("Ancho", 50f);	
		obstaculosParams.get(6).put("Alto", (float)ventana.height);
		obstaculosParams.get(6).put("Angulo", 0f);
		obstaculosParams.get(7).put("Posicion", new Point2D.Float(ventana.width/2, ventana.height + 5));
		obstaculosParams.get(7).put("Ancho", (float)ventana.width);	
		obstaculosParams.get(7).put("Alto", 20f);
		obstaculosParams.get(7).put("Angulo", 0f);
		obstaculosParams.get(8).put("Posicion", new Point2D.Float(ventana.width/2, 0));
		obstaculosParams.get(8).put("Ancho", (float)ventana.width);	
		obstaculosParams.get(8).put("Alto", 10f);
		obstaculosParams.get(8).put("Angulo", 0f);
	}
}

