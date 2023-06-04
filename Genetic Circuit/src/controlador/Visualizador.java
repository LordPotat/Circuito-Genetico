package controlador;

import java.awt.Color;

import modelo.Modelo;
import modelo.entidades.Entidad;
import vista.Vista;

/**
 * Se encarga de gestionar la representación visual en la interfaz de usuario, tanto del 
 * panel de control como la ventana gráfica, de los datos contenidos en el modelo
 * @author Alberto
 */
public class Visualizador {
	
	private Vista vista;
	private Modelo modelo;

	/**
	 * Crea el visualizador dándole acceso al modelo y a la vista
	 * @param modelo de donde obtiene los datos
	 * @param vista donde muestra los datos
	 */
	public Visualizador(Vista vista, Modelo modelo) {
		this.vista = vista;
		this.modelo = modelo;
	}

	/**
	 * Muestra en la ventana gráfica la ruta óptima (en el tiempo establecido) 
	 * desde el punto inicial de la población hasta la meta
	 * @param mejorEntidad: la entidad que ha logrado el objetivo establecido 
	 */
	void mostrarRutaOptima(Entidad mejorEntidad) {
		vista.getVentana().drawRutaOptima(mejorEntidad.getAdn().getGenes(), mejorEntidad.getTiempoObtenido());
		vista.getVentana().drawEntidad(mejorEntidad.getPosicion(), mejorEntidad.getVelocidad(), mejorEntidad.isMonitorizada());
	}
	
	/**
	 * Muestra una entidad en la ventana gráfica en la posición y dirección actual
	 * @param entidad que se debe mostrar
	 */
	public void mostrarEntidad(Entidad entidad) {
		vista.getVentana().drawEntidad(entidad.getPosicion(), entidad.getVelocidad(), entidad.isMonitorizada());
	}
	
	/**
	 * Muestra en pantalla sólo aquellas entidades que no hayan chocado. Se llama cuando
	 * se pausa la ejecución para que las entidades permanezcan dibujadas y no desaparezcan
	 * hasta reanudar.
	 */
	public void mostrarEntidadesActivas() {
		for(Entidad entidad : modelo.getPoblacion().getEntidades()) {
			if(!entidad.isHaChocado()) {
				mostrarEntidad(entidad);
			}
		}
	}
	
	/**
	 * Actualiza un campo (label) del panel de control con un nuevo valor
	 * @param <T> Tipo de valor
	 * @param label: nombre del campo que debe actualizar
	 * @param valor
	 */
	public <T> void actualizarPanel(String label, T valor) {
		vista.getPanelControl().setValor(label, valor);
	}
	
	/**
	 * Vacía los datos del panel de control correspondientes a la última generación
	 */
	void limpiarUltimaGeneracion() {
		modelo.getPoblacion().setEntidadMonitorizada(null); 
		actualizarPanel("TiempoRecordActual", 0);
		actualizarPanel("MejorAptitudActual", 0);
		actualizarPanel("MetasActual", 0);
		actualizarPanel("ColisionesActual", 0);
	}

	/**
	 * Actualiza en el panel de control todos los datos sobre una entidad
	 * que está siendo monitorizada actualmente
	 * @param entidad 
	 */
	void monitorizarEntidad(Entidad entidad) {
		actualizarPanel("Entidad", entidad.getIndice());
		//Redondeamos el valor de la distancia a dos decimales para no mostrar demasiados números
		actualizarPanel("DistanciaEntidad", redondearValor(entidad.getDistancia(), 2));
		actualizarPanel("DistanciaMinEntidad", redondearValor(entidad.getDistanciaMinima(), 2));
		//Para los vectores redondeamos hasta 4 decimales ya que debe mostrar más precisión
		actualizarPanel("Posicion", "("+ redondearValor(entidad.getPosicion().x, 4) +
				", " + redondearValor(entidad.getPosicion().y, 4) + ")");
		actualizarPanel("Velocidad", "("+ redondearValor(entidad.getVelocidad().x, 4) + 
				", " + redondearValor(entidad.getVelocidad().y, 4) + ")");
		actualizarPanel("Aceleracion", "("+ redondearValor(entidad.getAceleracion().x, 4) +
				", " + redondearValor(entidad.getAceleracion().y, 4) + ")");
		actualizarPanel("TiempoEntidad", entidad.getTiempoObtenido()); 
		actualizarPanel("AptitudEntidad", entidad.getAptitud());
		//Según la entidad tenga determinadas flags o no, mostrará en el estado en el que está
		String estado = entidad.isHaChocado() ? "Chocado" : entidad.isHaLlegado() ? "Llegado" : "Activa";
		actualizarPanel("EstadoEntidad", estado);
	}
	
	/**
	 * Redondea un valor decimal hasta el número de decimales indicado
	 * @param valor
	 * @param numDecimales 
	 * @return el valor redondeado
	 */
	private double redondearValor(float valor, int numDecimales) {
		double factor = Math.pow(10, numDecimales);
		return Math.round(valor * factor) / factor;
	}
	
	
	/**
	 * Vacía la entidad monitorizada y sus datos del panel de control cuando su generación ya no exista
	 */
	void limpiarEntidadMonitorizada() {
		vista.getPanelControl().getPanelDatosEntidad().setBackground(new Color(99, 9, 177));
		modelo.getPoblacion().setEntidadMonitorizada(null); 
		actualizarPanel("Entidad", "-");
		actualizarPanel("Posicion", "(0, 0)");
		actualizarPanel("Velocidad", "(0, 0)");
		actualizarPanel("Aceleracion", "(0, 0)");
		actualizarPanel("DistanciaEntidad", 0);
		actualizarPanel("DistanciaMinEntidad", 0);
		actualizarPanel("TiempoEntidad", 0); 
		actualizarPanel("AptitudEntidad", 0);
		actualizarPanel("EstadoEntidad", "-");
	}
	
}