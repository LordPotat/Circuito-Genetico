package main;
import java.awt.EventQueue;

import controlador.Controlador;

public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Controlador controlador = new Controlador();
					controlador.iniciar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
