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
import java.util.HashMap;

public class PanelControl extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel panel;
	private JPanel seccion2;
	private JPanel seccion1;
	private JPanel seccion3;
	private JLabel lblGeneracion;
	private JCheckBox cbModoAutomatico;
	private JButton btnProceder;
	private JButton btnReiniciar;
	private JButton btnPausar;
	private JSpinner spPoblacion;
	private JSpinner spMutacion;
	private JSpinner spTiempoObjetivo;
	private JSpinner spTiempoVida;
	private JLabel lblTiempoRecord;
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
	private JLabel lblTiempoEntidad;
	private JLabel lblTiempoRecordActual;
	private JLabel lblMejorAptitudActual;
	private JLabel lblMetasActual;
	private JLabel lblColisionesActual;
	private JLabel lblDatosTotal;
	private JLabel lblDatosActual;
	
	private HashMap<String, JLabel> mapaLabels;

	public PanelControl(Controlador controlador) {
		// Crear JFrame principal
        JFrame frame = new JFrame("Interfaz de Java Swing");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(PanelControl.class.getResource("/img/gene_icon.png")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400, 720));
        frame.setResizable(false);
        mapaLabels = new HashMap<String, JLabel>();

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
        lblGeneracion.setName("Generacion");
        mapaLabels.put(lblGeneracion.getName(), lblGeneracion);
        seccion1.add(lblGeneracion);

        btnProceder = new JButton("Empezar");
        btnProceder.addActionListener(controlador.new BtnEmpezarListener());
        seccion1.add(btnProceder);

        btnPausar = new JButton("Pausar");
        btnPausar.setEnabled(false);
        btnPausar.addActionListener(controlador.new BtnPausarListener());
        seccion1.add(btnPausar);
        
        btnReiniciar = new JButton("Reiniciar");
        btnReiniciar.setEnabled(false);
        btnReiniciar.addActionListener(controlador.new BtnReiniciarListener());
        seccion1.add(btnReiniciar);

        cbModoAutomatico = new JCheckBox("Modo Automático");
        cbModoAutomatico.addActionListener(controlador.new CbModoAutoListener());
        seccion1.add(cbModoAutomatico);
        
        
        // Segunda sección
        seccion2 = new JPanel();
        seccion2.setAlignmentX(Component.CENTER_ALIGNMENT);
        seccion2.setLayout(new BoxLayout(seccion2, BoxLayout.Y_AXIS));
        panel.add(seccion2);

        seccion2.add(new JLabel("Población total"));
        spPoblacion = new JSpinner(new SpinnerNumberModel(1000, 4, 15000, 50));
        spPoblacion.setName("NumEntidades");
        spPoblacion.addChangeListener(controlador.new SpParamListener());
        seccion2.add(spPoblacion);

        seccion2.add(new JLabel("Tasa de mutación (%)"));
        spMutacion = new JSpinner(new SpinnerNumberModel(20, 0, 100, 1));
        spMutacion.setName("TasaMutacion");
        spMutacion.addChangeListener(controlador.new SpParamListener());
        seccion2.add(spMutacion);

        seccion2.add(new JLabel("Tiempo objetivo (frames)"));
        spTiempoObjetivo = new JSpinner(new SpinnerNumberModel(140, 2, 2000, 1));
        spTiempoObjetivo.setName("TiempoObjetivo");
        spTiempoObjetivo.addChangeListener(controlador.new SpParamListener());
        seccion2.add(spTiempoObjetivo);

        seccion2.add(new JLabel("Tiempo de vida (frames)"));
        spTiempoVida = new JSpinner(new SpinnerNumberModel(400, 10, 2001, 5));
        spTiempoVida.setName("TiempoVida");
        spTiempoVida.addChangeListener(controlador.new SpParamListener());
        seccion2.add(spTiempoVida);

        // Tercera sección
        seccion3 = new JPanel();
        seccion3.setAlignmentX(Component.CENTER_ALIGNMENT);
        seccion3.setLayout(new BoxLayout(seccion3, BoxLayout.Y_AXIS));
        panel.add(seccion3);
        
        lblDatosTotal = new JLabel("Datos Totales");
        seccion3.add(lblDatosTotal);
   
        lblTiempoRecord = new JLabel("Tiempo record (frames): 0");
        lblTiempoRecord.setName("TiempoRecord");
        mapaLabels.put(lblTiempoRecord.getName(), lblTiempoRecord);
        seccion3.add(lblTiempoRecord);
        lblMejorAptitud = new JLabel("Mejor aptitud: 0");
        lblMejorAptitud.setName("MejorAptitud");
        mapaLabels.put(lblMejorAptitud.getName(), lblMejorAptitud);
        seccion3.add(lblMejorAptitud);
        lblMetas = new JLabel("Metas alcanzadas: 0");
        lblMetas.setName("Metas");
        mapaLabels.put(lblMetas.getName(), lblMetas);
        seccion3.add(lblMetas);
        lblColisiones = new JLabel("Colisiones: 0");
        lblColisiones.setName("Colisiones");
        mapaLabels.put(lblColisiones.getName(), lblColisiones);
        seccion3.add(lblColisiones);

        lblDatosActual = new JLabel("Datos Última Generación");
        seccion3.add(lblDatosActual);
        
        lblTiempoRecordActual = new JLabel("Tiempo record (frames): 0");
        lblTiempoRecordActual.setName("TiempoRecordActual");
        mapaLabels.put(lblTiempoRecordActual.getName(), lblTiempoRecordActual);
        seccion3.add(lblTiempoRecordActual);
        lblMejorAptitudActual = new JLabel("Mejor aptitud: 0");
        lblMejorAptitudActual.setName("MejorAptitudActual");
        mapaLabels.put(lblMejorAptitudActual.getName(), lblMejorAptitudActual);
        seccion3.add(lblMejorAptitudActual);
        lblMetasActual = new JLabel("Metas alcanzadas: 0");
        lblMetasActual.setName("MetasActual");
        mapaLabels.put(lblMetasActual.getName(), lblMetasActual);
        seccion3.add(lblMetasActual);
        lblColisionesActual = new JLabel("Colisiones: 0");
        lblColisionesActual.setName("ColisionesActual");
        mapaLabels.put(lblColisionesActual.getName(), lblColisionesActual);
        seccion3.add(lblColisionesActual);
        
        JPanel panelEntidad = new JPanel();
        panelEntidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelEntidad.setLayout(new BoxLayout(panelEntidad, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelEntidad);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        seccion3.add(scrollPane);

        lblEntidad = new JLabel("Entidad: -", SwingConstants.CENTER);
        lblEntidad.setName("Entidad");
        mapaLabels.put(lblEntidad.getName(), lblEntidad);
        panelEntidad.add(lblEntidad);
        lblPosicion = new JLabel("Posicion (px): [0,0,0]");
        lblPosicion.setName("Posicion");
        mapaLabels.put(lblPosicion.getName(), lblPosicion);
        panelEntidad.add(lblPosicion);
        lblVelocidad = new JLabel("Velocidad (px): [0,0,0]");
        lblVelocidad.setName("Velocidad");
        mapaLabels.put(lblVelocidad.getName(), lblVelocidad);
        panelEntidad.add(lblVelocidad);
        lblAceleracion = new JLabel("Aceleración (px): [0,0,0]");
        lblAceleracion.setName("Aceleracion");
        mapaLabels.put(lblAceleracion.getName(), lblAceleracion);
        panelEntidad.add(lblAceleracion);
        lblDistancia = new JLabel("Distancia: 0");
        lblDistancia.setName("DistanciaEntidad");
        mapaLabels.put(lblDistancia.getName(), lblDistancia);
        panelEntidad.add(lblDistancia);
        lblDistMinEntidad = new JLabel("Distancia mínima: 0");
        lblDistMinEntidad.setName("DistanciaMinEntidad");
        mapaLabels.put(lblDistMinEntidad.getName(), lblDistMinEntidad);
        panelEntidad.add(lblDistMinEntidad);
        lblEstado = new JLabel("Estado: -");
        lblEstado.setName("EstadoEntidad");
        mapaLabels.put(lblEstado.getName(), lblEstado);
        panelEntidad.add(lblEstado);
        lblTiempoEntidad = new JLabel("Tiempo obtenido: 0");
        lblTiempoEntidad.setName("TiempoEntidad");
        mapaLabels.put(lblTiempoEntidad.getName(), lblTiempoEntidad);
        panelEntidad.add(lblTiempoEntidad);
        lblAptitudEntidad = new JLabel("Aptitud: 0");
        lblAptitudEntidad.setName("AptitudEntidad");
        mapaLabels.put(lblAptitudEntidad.getName(), lblAptitudEntidad);
        panelEntidad.add(lblAptitudEntidad);

        // Mostrar la ventana
        frame.pack();
        frame.setVisible(true);
	}
	
	/**
	 * Actualiza el label que busca por el nombre pasado por parámetro con el valor indicado
	 * @param <T> tipo (generalmente numérico) de valor
	 * @param label: el nombre del label que debe actualizar
	 * @param valor: el valor que se le añadirá al label
	 */
	public <T> void setValor(String label, T valor) {
		JLabel lbl = obtenerLabelPorNombre(label); //Obtiene el JLabel por su nombre 
		//Si no lo encuentra no hace nada
		if(lbl == null) {
			return;
		}
		//Edita el texto del label con el nuevo valor parseado
		String txt = lbl.getText();
		lbl.setText(txt.split(":\\s*")[0] + ": " + String.valueOf(valor));
	}
	
	/**
	 * Devuelve un JLabel realizando una búsqueda por su nombre en el mapa correspondiente.
	 * @param nombre del label
	 * @return el JLabel que corresponde al nombre
	 */
	public JLabel obtenerLabelPorNombre(String nombre) {
        if (mapaLabels.containsKey(nombre)) {
                return (JLabel) mapaLabels.get(nombre);
        }
        else return null;
	}
	
	public int getTotalPoblacion() {
		return (int)spPoblacion.getValue();
	}

	public int getTasaMutacion() {
		return (int)spMutacion.getValue();
	}

	public int getTiempoObjetivo() {
		return (int)spTiempoObjetivo.getValue();
	}

	public int getTiempoVida() {
		return (int)spTiempoVida.getValue();
	}

	public JCheckBox getCbModoAutomatico() {
		return cbModoAutomatico;
	}

	public JButton getBtnProceder() {
		return btnProceder;
	}

	public JButton getBtnReiniciar() {
		return btnReiniciar;
	}

	public JButton getBtnPausar() {
		return btnPausar;
	}

	public JSpinner getSpTiempoObjetivo() {
		return spTiempoObjetivo;
	}
	
	 
}
