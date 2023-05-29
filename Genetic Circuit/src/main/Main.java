package main;
import java.awt.EventQueue;

import controlador.Controlador;

/**
 * Clase principal que inicia el programa
 * @author Alberto P�rez
 */
public class Main {
	
	public static void main(String[] args) {
		//Introduce todo el flujo de ejecuci�n en la cola de eventos
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//El controlador se encarga de iniciar y realizar todo
					Controlador controlador = new Controlador();
					controlador.iniciar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
