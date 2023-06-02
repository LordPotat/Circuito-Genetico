package controlador;
import java.awt.EventQueue;

/**
 * Clase principal que inicia el programa
 * @author Alberto Pérez
 */
public class Main {
	
	public static void main(String[] args) {
		//Introduce todo el flujo de ejecución en la cola de eventos
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				//El controlador se encarga de iniciar y realizar todo
				Controlador controlador = new Controlador();
				try {
					controlador.iniciarVista();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
