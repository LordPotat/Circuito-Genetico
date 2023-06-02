
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
 * Almacena todos los datos que determinan en qu� posici�n se encontrar�n los elementos
 * del sistema, as� como que atributos tendr�n para mostrarse e interactuar entre ellos
 * @author Alberto
 */
public class Circuito implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Punto de donde parten todas las entidades. Es un Point2D.Float en vez de un PVector
	 * porque PVector no permite la serializaci�n, pero esta clase es lo suficientemente 
	 * similar para poder convertir los datos de los vectores con facilidad al cargar el 
	 * objeto 
	 */
	private Point2D.Float spawn;
	
	/**
	 * Mapa que contiene todos los par�metros que definen la meta
	 */
	private HashMap<String, Object> metaParams;
	
	/**
	 * Mapa que contiene todos los par�metros que definen los obst�culos
	 */
	private ArrayList<HashMap<String, Object>> obstaculosParams;
	
	/**
	 * Crea un circuito con unos par�metros predefinidos para cada elemento, y despu�s almacena el
	 * objeto con todos los atributos en un fichero dentro de los archivos del programa
	 * Tras guardar el circuito, se podr� cargar (deserializando el objeto desde su fichero)
	 * @param nombreCircuito nombre que tendr� el fichero almacenado
	 * @param ventana para poder establecer posiciones relativas
	 */
	public static void guardarCircuito(String nombreCircuito, Ventana ventana) {
		//Crea un circuito vac�o
		Circuito circuito = new Circuito();
		//Llama a los m�todos para asignar todos los par�metros correspondientes
		circuito.asignarSpawn(ventana);
		circuito.crearMeta(ventana);
		circuito.crearObstaculos(ventana);
		//Le a�ade el nombre del circuito como el fichero en la ruta del fichero
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
	 * Carga desde un fichero con el nombre indicado un objeto que deserializar�
	 * para devolver un circuito con una serie de elementos ya definidos para que
	 * no tengamos que generarlo durante el programa 
	 * @param nombreCircuito nombre del fichero a cargar
	 * @return un objeto Circuito con todos los elementos ya establecidos
	 */
	public static Circuito cargarCircuito(String nombreCircuito) {
		Circuito circuito = null;
		//Obtiene la ruta del fichero a trav�s del nombre del circuito que buscamos
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
	 * @param ventana gr�fica para determinar la posici�n relativa del spawn
	 * @return el vector de posici�n del spawn
	 */
	public PVector setSpawn()  {
		//Convierte el Point2D.Float (serializable) en un objeto PVector (no serializable)
		return new PVector(spawn.x, spawn.y); 
	}
	
	/**
	 * Establece los par�metros de inicializaci�n de la meta
	 * @param ventana gr�fica para determinar la posici�n relativa de la meta
	 * @return el mapa con los par�metros
	 */
	public HashMap<String, Object> setupMeta(Ventana ventana) {
		HashMap<String, Object> metaParams = new HashMap<String, Object>();
		//Convierte el Point2D.Float de la posici�n a un PVector
		Point2D.Float posicion = (Float) this.metaParams.get("Posicion");
		metaParams.put("Posicion", new PVector(posicion.x, posicion.y));
		//Asigna el resto de par�metros almacenados del fichero
		metaParams.put("Ancho", this.metaParams.get("Ancho"));	
		metaParams.put("Alto", this.metaParams.get("Alto"));
		return metaParams;
	}
	
	/**
	 * Establece los par�metros de inicializaci�n de los obst�culos
	 * @param ventana gr�fica para determinar la posici�n relativa de los obst�culos
	 * @return el mapa con los par�metros
	 */
	public ArrayList<HashMap<String, Object>> setupObstaculos(Ventana ventana) {
		//Copia todos los obst�culos del objeto del fichero en una nueva colecci�n
		ArrayList<HashMap<String, Object>> obstaculosParams = new ArrayList<HashMap<String, Object>>(this.obstaculosParams);
		/* Por cada mapa de par�metros que definen un obst�culo, debe convertir el Point2D.Float
		 * de la posici�n a un PVector
		 */
		for(int i=0; i < obstaculosParams.size(); i++) {
			HashMap<String, Object> obstaculo = obstaculosParams.get(i);
			Point2D.Float posicion = (Float) obstaculo.get("Posicion");
			obstaculo.put("Posicion", new PVector(posicion.x, posicion.y));
		}
		return obstaculosParams;
	}
	
	/**
	 * Crea una posici�n de inicio para las entidades relativo a la ventana
	 * Para poder serializarlo, el vector se crea como Point2D.Float
	 * @param ventana
	 */
	private void asignarSpawn(Ventana ventana)  {
		spawn = new Point2D.Float(ventana.width/2, ventana.height-30);
	}
	
	/**
	 * Crea una meta que se mostrar� en una posici�n relativa a la ventana
	 * @param ventana
	 */
	private void crearMeta(Ventana ventana) {
		metaParams = new HashMap<String, Object>();
		metaParams.put("Posicion", new Point2D.Float(ventana.width/2, 40));
		metaParams.put("Ancho", 50f);	
		metaParams.put("Alto", 50f);
	}
	
	/**
	 * Crea todos los obst�culos del circuito en posiciones relativas a la ventana
	 * y con un tama�o y rotaci�n determinadas
	 * @param ventana
	 */
	private void crearObstaculos(Ventana ventana) {
		//Inicia una lista con todos los obst�culos que deber� tener el circuito
		obstaculosParams = new ArrayList<HashMap<String, Object>>();
		int numObstaculos = 9;
		for(int i=0; i < numObstaculos; i++) {
			obstaculosParams.add(new HashMap<String, Object>());
		}
		//Ajusta los par�metros para cada uno de los obst�culos individualmente
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

