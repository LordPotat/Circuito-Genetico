package controlador;
import java.awt.EventQueue;

/**
 * Clase principal que inicia el programa
 * @author Alberto Pérez
 */
public class Main {
	
	/**
	 * Punto de entrada al programa. Según los argumentos que reciba iniciará el controlador
	 * principal en un modo u otro y le delegará el flujo de ejecución
	 * @param args
	 */
	public static void main(String[] args) {
		//Introduce todo el flujo de ejecución en la cola de eventos para la interfaz de Swing
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				//Obtiene el modo de ejecución del controlador a partir de los argumentos de main
				String modoEjecucion = determinarModoEjecucion(args);
				//Inicia el controlador del programa en el modo indicado
				Controlador controlador = new Controlador(modoEjecucion);
				try {
					//El programa no se iniciará hasta que se llame a este método del controlador
					controlador.iniciarControlador();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			/**
			 * Si recibe como primer argumento "EDITOR", iniciará el programa en modo editor 
			 * de circuitos. En caso contrario, lo hará en modo normal y seguirá su flujo
			 * de ejecución estándar.
			 * @param args los argumentos que recibe el programa
			 */
			private String determinarModoEjecucion(String[] args) {
				String modoEjecucion = "";
				//Si tienea al menos un argumento y es "EDITOR", ese será el modo
				if (args.length > 0 && args[0].equals("EDITOR")) {
					modoEjecucion = "EDITOR";
				} else {
					//Para cualquier otro caso el modo el "NORMAL"
					modoEjecucion = "NORMAL";
				}
				return modoEjecucion;
			}
		});
	}

}
