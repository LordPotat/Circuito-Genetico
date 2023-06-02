package controlador;
import java.awt.EventQueue;

/**
 * Clase principal que inicia el programa
 * @author Alberto P�rez
 */
public class Main {
	
	/**
	 * Punto de entrada al programa. Seg�n los argumentos que reciba iniciar� el controlador
	 * principal en un modo u otro y le delegar� el flujo de ejecuci�n
	 * @param args
	 */
	public static void main(String[] args) {
		//Introduce todo el flujo de ejecuci�n en la cola de eventos para la interfaz de Swing
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				//Obtiene el modo de ejecuci�n del controlador a partir de los argumentos de main
				String modoEjecucion = determinarModoEjecucion(args);
				//Inicia el controlador del programa en el modo indicado
				Controlador controlador = new Controlador(modoEjecucion);
				try {
					//El programa no se iniciar� hasta que se llame a este m�todo del controlador
					controlador.iniciarControlador();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			/**
			 * Si recibe como primer argumento "EDITOR", iniciar� el programa en modo editor 
			 * de circuitos. En caso contrario, lo har� en modo normal y seguir� su flujo
			 * de ejecuci�n est�ndar.
			 * @param args los argumentos que recibe el programa
			 */
			private String determinarModoEjecucion(String[] args) {
				String modoEjecucion = "";
				//Si tienea al menos un argumento y es "EDITOR", ese ser� el modo
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
