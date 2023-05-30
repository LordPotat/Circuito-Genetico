package vista.panel_control;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import controlador.Controlador;
import java.awt.Toolkit;

public class PanelControl extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Controlador controlador;
	private JPanel contentPane;
	
	private JPanel panel;
	private JPanel seccion2;
	private JPanel seccion1;
	private JPanel seccion3;
	private JLabel lblGeneracion;
	private JCheckBox cbModoAutomatico;
	private JButton btnEmpezar;
	private JButton btnReiniciar;
	private JSpinner spPoblacion;
	private JSpinner spMutacion;
	private JSpinner spTiempoObjetivo;
	private JSpinner spTiempoVida;
	private JLabel lblTiempoRecord;
	private JLabel lblDistMin;
	private JLabel lblMejorAptitud;
	private JLabel lblMetas;
	private JLabel lblColisiones;
	private JLabel lblEntidad;
	private JLabel lblPosicion;
	private JLabel lblVelocidad;
	private JLabel lblAceleracion;
	private JLabel lblDistancia;
	private JLabel lblDistMinEntidad;
	private JLabel lblEstado;
	private JLabel lblAptitudEntidad;


	public PanelControl(Controlador controlador) {
		// Crear JFrame principal
        JFrame frame = new JFrame("Interfaz de Java Swing");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(PanelControl.class.getResource("/img/gene_icon.png")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400, 720));
        frame.setResizable(false);

        // Crear JPanel principal
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.getContentPane().add(panel);

        // Primera sección
        seccion1 = new JPanel();
        seccion1.setAlignmentX(Component.CENTER_ALIGNMENT);
        seccion1.setLayout(new BoxLayout(seccion1, BoxLayout.Y_AXIS));
        panel.add(seccion1);

        lblGeneracion = new JLabel("Generación: 0");
        seccion1.add(lblGeneracion);

        cbModoAutomatico = new JCheckBox("Modo Automático");
        seccion1.add(cbModoAutomatico);

        btnEmpezar = new JButton("Comenzar");
        seccion1.add(btnEmpezar);

        btnReiniciar = new JButton("Reiniciar");
        seccion1.add(btnReiniciar);

        // Segunda sección
        seccion2 = new JPanel();
        seccion2.setAlignmentX(Component.CENTER_ALIGNMENT);
        seccion2.setLayout(new BoxLayout(seccion2, BoxLayout.Y_AXIS));
        panel.add(seccion2);

        seccion2.add(new JLabel("Población total"));
        spPoblacion = new JSpinner(new SpinnerNumberModel(2, 2, 10000, 1));
        seccion2.add(spPoblacion);

        seccion2.add(new JLabel("Tasa de mutación (%)"));
        spMutacion = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        seccion2.add(spMutacion);

        seccion2.add(new JLabel("Tiempo objetivo (frames)"));
        spTiempoObjetivo = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        seccion2.add(spTiempoObjetivo);

        seccion2.add(new JLabel("Tiempo de vida (frames)"));
        spTiempoVida = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        seccion2.add(spTiempoVida);

        // Tercera sección
        seccion3 = new JPanel();
        seccion3.setAlignmentX(Component.CENTER_ALIGNMENT);
        seccion3.setLayout(new BoxLayout(seccion3, BoxLayout.Y_AXIS));
        panel.add(seccion3);

        lblTiempoRecord = new JLabel("Tiempo record (frames):");
        seccion3.add(lblTiempoRecord);
        lblDistMin = new JLabel("Distancia mínima:");
        seccion3.add(lblDistMin);
        lblMejorAptitud = new JLabel("Mejor aptitud:");
        seccion3.add(lblMejorAptitud);
        lblMetas = new JLabel("Metas alcanzadas:");
        seccion3.add(lblMetas);
        lblColisiones = new JLabel("Colisiones:");
        seccion3.add(lblColisiones);

        JPanel panelEntidad = new JPanel();
        panelEntidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelEntidad.setLayout(new BoxLayout(panelEntidad, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelEntidad);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        seccion3.add(scrollPane);

        lblEntidad = new JLabel("Entidad", SwingConstants.CENTER);
        panelEntidad.add(lblEntidad);
        lblPosicion = new JLabel("Posicion (px): (0,0)");
        panelEntidad.add(lblPosicion);
        lblVelocidad = new JLabel("Velocidad (px/frame): 0");
        panelEntidad.add(lblVelocidad);
        lblAceleracion = new JLabel("Aceleración (px/frame): 0");
        panelEntidad.add(lblAceleracion);
        lblDistancia = new JLabel("Distancia:");
        panelEntidad.add(lblDistancia);
        lblDistMinEntidad = new JLabel("Distancia mínima:");
        panelEntidad.add(lblDistMinEntidad);
        lblEstado = new JLabel("Estado:");
        panelEntidad.add(lblEstado);
        JLabel lblTiempoEntidad = new JLabel("Tiempo obtenido:");
        panelEntidad.add(lblTiempoEntidad);
        lblAptitudEntidad = new JLabel("Aptitud:");
        panelEntidad.add(lblAptitudEntidad);

        // Mostrar la ventana
        frame.pack();
        frame.setVisible(true);
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public Controlador getControlador() {
		return controlador;
	}


	public JPanel getContentPane() {
		return contentPane;
	}


	public JPanel getPanel() {
		return panel;
	}


	public JPanel getSeccion2() {
		return seccion2;
	}


	public JPanel getSeccion1() {
		return seccion1;
	}


	public JPanel getSeccion3() {
		return seccion3;
	}


	public JLabel getLblGeneracion() {
		return lblGeneracion;
	}


	public JCheckBox getCbModoAutomatico() {
		return cbModoAutomatico;
	}


	public JButton getBtnEmpezar() {
		return btnEmpezar;
	}


	public JButton getBtnReiniciar() {
		return btnReiniciar;
	}


	public JSpinner getSpPoblacion() {
		return spPoblacion;
	}


	public JSpinner getSpMutacion() {
		return spMutacion;
	}


	public JSpinner getSpTiempoObjetivo() {
		return spTiempoObjetivo;
	}


	public JSpinner getSpTiempoVida() {
		return spTiempoVida;
	}


	public JLabel getLblTiempoRecord() {
		return lblTiempoRecord;
	}


	public JLabel getLblDistMin() {
		return lblDistMin;
	}


	public JLabel getLblMejorAptitud() {
		return lblMejorAptitud;
	}


	public JLabel getLblMetas() {
		return lblMetas;
	}


	public JLabel getLblColisiones() {
		return lblColisiones;
	}


	public JLabel getLblEntidad() {
		return lblEntidad;
	}


	public JLabel getLblPosicion() {
		return lblPosicion;
	}


	public JLabel getLblVelocidad() {
		return lblVelocidad;
	}


	public JLabel getLblAceleracion() {
		return lblAceleracion;
	}


	public JLabel getLblDistancia() {
		return lblDistancia;
	}


	public JLabel getLblDistMinEntidad() {
		return lblDistMinEntidad;
	}


	public JLabel getLblEstado() {
		return lblEstado;
	}


	public JLabel getLblAptitudEntidad() {
		return lblAptitudEntidad;
	}

	
}
