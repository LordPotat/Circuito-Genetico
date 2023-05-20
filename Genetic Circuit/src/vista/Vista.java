package vista;

import vista.panel_control.PanelControl;
import vista.ventana_grafica.Ventana;

public class Vista {
	
	private Ventana ventana;
	private PanelControl panelControl;
	
	public Vista() {
		String[] processingArgs = {"Circuito Gen�tico"};
		ventana = Ventana.crearVentana(processingArgs);
		panelControl = new PanelControl();
		panelControl.setVisible(true);
	}
}
