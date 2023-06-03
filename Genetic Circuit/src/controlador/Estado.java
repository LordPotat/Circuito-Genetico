package controlador;

/**
 * Estados que puede tener el proceso visiblemente en la ventana gráfica en función de los
 * controles utilizados del panel de control y del propio proceso evolutivo
 * @author Alberto
 */
public enum Estado {
	//233,106,0
	//Cada estado tiene su propio texto y color asignado
	EN_ESPERA("En Espera", new int[]{4,84,151}), REALIZANDO_CICLO("Realizando Ciclo",new int[]{233,106,0}),
	PAUSADO("Pausado", new int[]{206,0,69}), FINALIZADO("Finalizado", new int[]{0,150,100}); 
	
	/**
	 * Representación textual que muestra en la ventana el estado
	 */
	private String texto;
	
	/**
	 * Color en formato RGB con el que se muestra en la ventana 
	 */
	private int[] color;
	
	//Lo llama internamente cada elemento para inicializar sus atributos
	Estado(String texto, int[] color) {
		this.texto = texto;
		this.color = color;
	}
	
	/**
	 * Convierte el enum en texto más "amigable" para el usuario
	 * @return el texto transformado
	 */
	public String getTexto() {
		return texto;
	}
	
	/**
	 * Devuelve un color RBG para diferenciar a cada estado en la ventana
	 * @return el array que representa los tres valores RGB
	 */
	public int[] getColor() {
		return color;
	}
}