package vista.panel_control;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
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
import javax.swing.DefaultComboBoxModel;
import controlador.Controlador;
import controlador.ControladorEventos;

import java.awt.Toolkit;
import java.util.HashMap;
import javax.swing.JComboBox;
import java.io.File;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;

public class PanelControl {
	
	private JPanel panelPrincipal;
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
	private JPanel panelContenido;
	
	private HashMap<String, JLabel> mapaLabels;
	private JComboBox<String> cBoxCircuito;
	private JButton btnSalir;

	private Controlador controlador;
	private JLabel lblCircuitos;
	private JLabel lblTitulo;

	public PanelControl(Controlador controlador) {
		
		this.controlador = controlador;
		
		// Crear JFrame principal
        JFrame frmCircuitoGenetico = new JFrame("Circuito Gen\u00E9tico - Panel de Control\r\n");
        frmCircuitoGenetico.setLocation(new Point(0, 0));
        frmCircuitoGenetico.setName("frameCircuitoGenetico");
        frmCircuitoGenetico.setTitle("Panel de Control");
        frmCircuitoGenetico.setIconImage(Toolkit.getDefaultToolkit().getImage(PanelControl.class.getResource("/img/gene_icon.png")));
        frmCircuitoGenetico.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmCircuitoGenetico.setPreferredSize(new Dimension(325, 720));
        frmCircuitoGenetico.setResizable(false);
        mapaLabels = new HashMap<String, JLabel>();

        // Crear JPanel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setBackground(new Color(46, 10, 84));
        panelPrincipal.setLayout(new BorderLayout(0, 0));
        frmCircuitoGenetico.setContentPane(panelPrincipal);

        JPanel panelTop = new JPanel();
        panelTop.setOpaque(false);
        panelPrincipal.add(panelTop, BorderLayout.NORTH);
        
        lblTitulo = new JLabel("Circuito Gen\u00E9tico");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(255, 255, 255));
        lblTitulo.setFont(new Font("Lato", Font.BOLD, 15));
        panelTop.add(lblTitulo);
        
        JPanel bordeIzq = new JPanel();
        bordeIzq.setOpaque(false);
        panelPrincipal.add(bordeIzq, BorderLayout.WEST);
        
        JPanel bordeDrc = new JPanel();
        bordeDrc.setOpaque(false);
        panelPrincipal.add(bordeDrc, BorderLayout.EAST);

        JPanel bordeAbajo = new JPanel();
        bordeAbajo.setOpaque(false);
        panelPrincipal.add(bordeAbajo, BorderLayout.SOUTH);
        
        panelContenido = new JPanel();
        panelContenido.setOpaque(false);
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        
        // Primera sección
        seccion1 = new JPanel();
        seccion1.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Controles", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(255, 255, 255)));
        seccion1.setBackground(new Color(99, 9, 177));
        seccion1.setAlignmentX(Component.CENTER_ALIGNMENT);
        seccion1.setLayout(new BoxLayout(seccion1, BoxLayout.Y_AXIS));
        panelContenido.add(seccion1);

        lblGeneracion = new JLabel("Generación: 0");
        lblGeneracion.setForeground(new Color(255, 255, 255));
        lblGeneracion.setFont(new Font("Lato", Font.BOLD, 12));
        lblGeneracion.setName("Generacion");
        mapaLabels.put(lblGeneracion.getName(), lblGeneracion);
        seccion1.add(lblGeneracion);

        cbModoAutomatico = new JCheckBox("Modo Automático");
        cbModoAutomatico.setVerticalTextPosition(SwingConstants.BOTTOM);
        cbModoAutomatico.setHorizontalAlignment(SwingConstants.TRAILING);
        cbModoAutomatico.setHorizontalTextPosition(SwingConstants.LEADING);
        cbModoAutomatico.setForeground(new Color(255, 255, 255));
        cbModoAutomatico.setFont(new Font("Lato", Font.BOLD, 12));
        cbModoAutomatico.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cbModoAutomatico.setToolTipText("");
        cbModoAutomatico.setFocusable(false);
        seccion1.add(cbModoAutomatico);
        
        btnProceder = new JButton("Empezar");
        btnProceder.setFont(new Font("Lato", Font.BOLD, 12));
        btnProceder.setBackground(new Color(141, 71, 201));
        btnProceder.setForeground(new Color(255, 255, 255));
        btnProceder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnProceder.setToolTipText("Comienza una nueva generaci\u00F3n ");
        btnProceder.setFocusable(false);
        seccion1.add(btnProceder);

        btnPausar = new JButton("Pausar");
        btnPausar.setFont(new Font("Lato", Font.BOLD, 12));
        btnPausar.setBackground(new Color(141, 71, 201));
        btnPausar.setForeground(new Color(255, 255, 255));
        btnPausar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPausar.setToolTipText("Pausa/Reanuda el movimiento de las entidades");
        btnPausar.setFocusable(false);
        btnPausar.setEnabled(false);
        seccion1.add(btnPausar);
        
        btnReiniciar = new JButton("Reiniciar");
        btnReiniciar.setFont(new Font("Lato", Font.BOLD, 12));
        btnReiniciar.setBackground(new Color(141, 71, 201));
        btnReiniciar.setForeground(new Color(255, 255, 255));
        btnReiniciar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReiniciar.setToolTipText("Termina de ejecutar el proceso evolutivo y vuelve al estado inicial");
        btnReiniciar.setFocusable(false);
        btnReiniciar.setEnabled(false);
        seccion1.add(btnReiniciar);
        
        btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Lato", Font.BOLD, 12));
        btnSalir.setBackground(new Color(141, 71, 201));
        btnSalir.setForeground(new Color(255, 255, 255));
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.setToolTipText("Cierra el programa");
        btnSalir.setFocusable(false);
        seccion1.add(btnSalir);
 
        Component separador1 = Box.createVerticalStrut(10);
        panelContenido.add(separador1);
        
        // Segunda sección
        seccion2 = new JPanel();
        seccion2.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Configuraci\u00F3n", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
        seccion2.setAlignmentX(Component.CENTER_ALIGNMENT);
        seccion2.setBackground(new Color(99, 9, 177));
        seccion2.setLayout(new BoxLayout(seccion2, BoxLayout.Y_AXIS));
        panelContenido.add(seccion2);
        
        lblCircuitos = new JLabel("Circuito");
        lblCircuitos.setForeground(new Color(255, 255, 255));
        lblCircuitos.setFont(new Font("Lato", Font.BOLD, 12));
        lblCircuitos.setLabelFor(cBoxCircuito);
        seccion2.add(lblCircuitos);
        
        cBoxCircuito = rellenarCboxCircuito();
        cBoxCircuito.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cBoxCircuito.setFont(new Font("Lato", Font.BOLD, 12));
        cBoxCircuito.setToolTipText("Selecciona un circuito para las entidades ");
        cBoxCircuito.setSelectedIndex(0);
        cBoxCircuito.setMaximumRowCount(10);
        cBoxCircuito.setEditable(true);
        seccion2.add(cBoxCircuito);

        JLabel lblPoblacionTotal = new JLabel("Población total");
        lblPoblacionTotal.setForeground(new Color(255, 255, 255));
        lblPoblacionTotal.setFont(new Font("Lato", Font.BOLD, 12));
        seccion2.add(lblPoblacionTotal);
        spPoblacion = new JSpinner(new SpinnerNumberModel(1000, 4, 15000, 50));
        lblPoblacionTotal.setLabelFor(spPoblacion);
        spPoblacion.setFont(new Font("Lato", Font.BOLD, 12));
        spPoblacion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spPoblacion.setToolTipText("Modifica el n\u00FAmero de entidades que se generan");
        spPoblacion.setName("NumEntidades");
        seccion2.add(spPoblacion);

        JLabel lblTasaMutacion = new JLabel("Tasa de mutación (%)");
        lblTasaMutacion.setForeground(new Color(255, 255, 255));
        lblTasaMutacion.setFont(new Font("Lato", Font.BOLD, 12));
        seccion2.add(lblTasaMutacion);
        spMutacion = new JSpinner(new SpinnerNumberModel(20, 0, 100, 1));
        lblTasaMutacion.setLabelFor(spMutacion);
        spMutacion.setFont(new Font("Lato", Font.BOLD, 12));
        spMutacion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spMutacion.setToolTipText("Modifica la probabilidad de que muten los genes");
        spMutacion.setName("TasaMutacion");
        seccion2.add(spMutacion);

        JLabel lblTiempoObjetivo = new JLabel("Tiempo objetivo (frames)");
        lblTiempoObjetivo.setForeground(new Color(255, 255, 255));
        lblTiempoObjetivo.setFont(new Font("Lato", Font.BOLD, 12));
        seccion2.add(lblTiempoObjetivo);
        spTiempoObjetivo = new JSpinner(new SpinnerNumberModel(140, 2, 2000, 1));
        lblTiempoObjetivo.setLabelFor(spTiempoObjetivo);
        spTiempoObjetivo.setFont(new Font("Lato", Font.BOLD, 12));
        spTiempoObjetivo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spTiempoObjetivo.setToolTipText("Modifica el tiempo en frames que deben lograr las entidades para llegar a la meta");
        spTiempoObjetivo.setName("TiempoObjetivo");
        seccion2.add(spTiempoObjetivo);

        JLabel lblTiempoVida = new JLabel("Tiempo de vida (frames)");
        lblTiempoVida.setForeground(new Color(255, 255, 255));
        lblTiempoVida.setFont(new Font("Lato", Font.BOLD, 12));
        seccion2.add(lblTiempoVida);
        spTiempoVida = new JSpinner(new SpinnerNumberModel(400, 10, 2001, 5));
        lblTiempoVida.setLabelFor(spTiempoVida);
        spTiempoVida.setFont(new Font("Lato", Font.BOLD, 12));
        spTiempoVida.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spTiempoVida.setToolTipText("Modifica el tiempo en frames que viven las entidades");
        spTiempoVida.setName("TiempoVida");
        seccion2.add(spTiempoVida);

        Component separador2 = Box.createVerticalStrut(10);
        panelContenido.add(separador2);
        
        // Tercera sección
        seccion3 = new JPanel();
        seccion3.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Monitorizaci\u00F3n", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(255, 255, 255)));
        seccion3.setBackground(new Color(99, 9, 177));
        seccion3.setAlignmentX(Component.CENTER_ALIGNMENT);
        seccion3.setLayout(new BoxLayout(seccion3, BoxLayout.Y_AXIS));
        panelContenido.add(seccion3);
        
        lblDatosTotal = new JLabel("Datos Totales");
        lblDatosTotal.setForeground(new Color(255, 255, 255));
        lblDatosTotal.setFont(new Font("Lato", Font.BOLD, 12));
        seccion3.add(lblDatosTotal);
   
        lblTiempoRecord = new JLabel("Tiempo record (frames): 0");
        lblTiempoRecord.setForeground(new Color(255, 255, 255));
        lblTiempoRecord.setFont(new Font("Lato", Font.BOLD, 12));
        lblTiempoRecord.setName("TiempoRecord");
        mapaLabels.put(lblTiempoRecord.getName(), lblTiempoRecord);
        seccion3.add(lblTiempoRecord);
        lblMejorAptitud = new JLabel("Mejor aptitud: 0");
        lblMejorAptitud.setForeground(new Color(255, 255, 255));
        lblMejorAptitud.setFont(new Font("Lato", Font.BOLD, 12));
        lblMejorAptitud.setName("MejorAptitud");
        mapaLabels.put(lblMejorAptitud.getName(), lblMejorAptitud);
        seccion3.add(lblMejorAptitud);
        lblMetas = new JLabel("Metas alcanzadas: 0");
        lblMetas.setForeground(new Color(255, 255, 255));
        lblMetas.setFont(new Font("Lato", Font.BOLD, 12));
        lblMetas.setName("Metas");
        mapaLabels.put(lblMetas.getName(), lblMetas);
        seccion3.add(lblMetas);
        lblColisiones = new JLabel("Colisiones: 0");
        lblColisiones.setForeground(new Color(255, 255, 255));
        lblColisiones.setFont(new Font("Lato", Font.BOLD, 12));
        lblColisiones.setName("Colisiones");
        mapaLabels.put(lblColisiones.getName(), lblColisiones);
        seccion3.add(lblColisiones);

        lblDatosActual = new JLabel("Datos Última Generación");
        lblDatosActual.setForeground(new Color(255, 255, 255));
        lblDatosActual.setFont(new Font("Lato", Font.BOLD, 12));
        seccion3.add(lblDatosActual);
        
        lblTiempoRecordActual = new JLabel("Tiempo record (frames): 0");
        lblTiempoRecordActual.setForeground(new Color(255, 255, 255));
        lblTiempoRecordActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblTiempoRecordActual.setName("TiempoRecordActual");
        mapaLabels.put(lblTiempoRecordActual.getName(), lblTiempoRecordActual);
        seccion3.add(lblTiempoRecordActual);
        lblMejorAptitudActual = new JLabel("Mejor aptitud: 0");
        lblMejorAptitudActual.setForeground(new Color(255, 255, 255));
        lblMejorAptitudActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblMejorAptitudActual.setName("MejorAptitudActual");
        mapaLabels.put(lblMejorAptitudActual.getName(), lblMejorAptitudActual);
        seccion3.add(lblMejorAptitudActual);
        lblMetasActual = new JLabel("Metas alcanzadas: 0");
        lblMetasActual.setForeground(new Color(255, 255, 255));
        lblMetasActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblMetasActual.setName("MetasActual");
        mapaLabels.put(lblMetasActual.getName(), lblMetasActual);
        seccion3.add(lblMetasActual);
        lblColisionesActual = new JLabel("Colisiones: 0");
        lblColisionesActual.setForeground(new Color(255, 255, 255));
        lblColisionesActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblColisionesActual.setName("ColisionesActual");
        mapaLabels.put(lblColisionesActual.getName(), lblColisionesActual);
        seccion3.add(lblColisionesActual);
        
        JPanel panelEntidad = new JPanel();
        panelEntidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelEntidad.setLayout(new BoxLayout(panelEntidad, BoxLayout.Y_AXIS));
        panelEntidad.setBackground(new Color(120, 33, 195));
        JScrollPane scrollPane = new JScrollPane(panelEntidad);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        seccion3.add(scrollPane);

        lblEntidad = new JLabel("Entidad: -", SwingConstants.CENTER);
        lblEntidad.setForeground(new Color(255, 255, 255));
        lblEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblEntidad.setName("Entidad");
        mapaLabels.put(lblEntidad.getName(), lblEntidad);
        panelEntidad.add(lblEntidad);
        lblPosicion = new JLabel("Posicion (px): (0,0)");
        lblPosicion.setForeground(new Color(255, 255, 255));
        lblPosicion.setFont(new Font("Lato", Font.BOLD, 12));
        lblPosicion.setName("Posicion");
        mapaLabels.put(lblPosicion.getName(), lblPosicion);
        panelEntidad.add(lblPosicion);
        lblVelocidad = new JLabel("Velocidad (px): (0,0)");
        lblVelocidad.setForeground(new Color(255, 255, 255));
        lblVelocidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblVelocidad.setName("Velocidad");
        mapaLabels.put(lblVelocidad.getName(), lblVelocidad);
        panelEntidad.add(lblVelocidad);
        lblAceleracion = new JLabel("Aceleración (px): (0,0)");
        lblAceleracion.setForeground(new Color(255, 255, 255));
        lblAceleracion.setFont(new Font("Lato", Font.BOLD, 12));
        lblAceleracion.setName("Aceleracion");
        mapaLabels.put(lblAceleracion.getName(), lblAceleracion);
        panelEntidad.add(lblAceleracion);
        lblDistancia = new JLabel("Distancia: 0");
        lblDistancia.setForeground(new Color(255, 255, 255));
        lblDistancia.setFont(new Font("Lato", Font.BOLD, 12));
        lblDistancia.setName("DistanciaEntidad");
        mapaLabels.put(lblDistancia.getName(), lblDistancia);
        panelEntidad.add(lblDistancia);
        lblDistMinEntidad = new JLabel("Distancia mínima: 0");
        lblDistMinEntidad.setForeground(new Color(255, 255, 255));
        lblDistMinEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblDistMinEntidad.setName("DistanciaMinEntidad");
        mapaLabels.put(lblDistMinEntidad.getName(), lblDistMinEntidad);
        panelEntidad.add(lblDistMinEntidad);
        lblEstado = new JLabel("Estado: -");
        lblEstado.setForeground(new Color(255, 255, 255));
        lblEstado.setFont(new Font("Lato", Font.BOLD, 12));
        lblEstado.setName("EstadoEntidad");
        mapaLabels.put(lblEstado.getName(), lblEstado);
        panelEntidad.add(lblEstado);
        lblTiempoEntidad = new JLabel("Tiempo obtenido: 0");
        lblTiempoEntidad.setForeground(new Color(255, 255, 255));
        lblTiempoEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblTiempoEntidad.setName("TiempoEntidad");
        mapaLabels.put(lblTiempoEntidad.getName(), lblTiempoEntidad);
        panelEntidad.add(lblTiempoEntidad);
        lblAptitudEntidad = new JLabel("Aptitud: 0");
        lblAptitudEntidad.setForeground(new Color(255, 255, 255));
        lblAptitudEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblAptitudEntidad.setName("AptitudEntidad");
        mapaLabels.put(lblAptitudEntidad.getName(), lblAptitudEntidad);
        panelEntidad.add(lblAptitudEntidad);

        // Mostrar la ventana
        frmCircuitoGenetico.pack();
        // Obtener el alto de la pantalla
        Dimension dimensionesPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int altoPantalla = dimensionesPantalla.height;
        // Obtener el alto del JFrame
        int altoFrame = frmCircuitoGenetico.getHeight();
        // Calcular la posición vertical para centrar el JFrame
        int y = (altoPantalla - altoFrame) / 2;
        // Establecer la posición del JFrame centrado verticalmente
        frmCircuitoGenetico.setLocation(frmCircuitoGenetico.getX(), y);
        frmCircuitoGenetico.setVisible(true);
	}
	
	/**
	 * Añade listeners a cada uno de los controles que lo necesita para que ejecuten
	 * los eventos del controlador que le corresponden a cada uno
	 */
	public void asignarEventos() {
		//Obtiene el controlador que gestiona los eventos de la interfaz
		ControladorEventos controladorEventos = controlador.getControladorEventos();
		//Asigna los eventos a los listeners de cada evento
		btnProceder.addActionListener((e) -> controladorEventos.empezar());
        btnPausar.addActionListener((e) -> controladorEventos.pausar());
        btnReiniciar.addActionListener((e) -> controladorEventos.reiniciar());
        btnSalir.addActionListener((e) -> controladorEventos.salir());
        cbModoAutomatico.addActionListener((e) -> controladorEventos.cambiarModo(e));
        cBoxCircuito.addItemListener((e) -> controladorEventos.elegirCircuito(e));
        spPoblacion.addChangeListener((e) -> controladorEventos.modificarParametro(e));
        spMutacion.addChangeListener((e) -> controladorEventos.modificarParametro(e));
        spTiempoObjetivo.addChangeListener((e) -> controladorEventos.modificarParametro(e));
        spTiempoVida.addChangeListener((e) -> controladorEventos.modificarParametro(e));
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
	
	/**
	 * Añade al combo box de circuitos todos los nombres de los ficheros de la carpeta
	 * del proyecto en el que se encuentran almacenados, para que se puedan seleccionar
	 * @return el combo box con la lista de circuitos seleccionables
	 */
	private JComboBox<String> rellenarCboxCircuito() {
		//'res/circuits' es la ruta del proyecto donde se encuentran
		File carpetaCircuitos = new File("res/circuits"); 
		//Obtiene los nombres de los ficheros de cada circuito de la carpeta
		String[] circuitos = carpetaCircuitos.list(); 
		//Elimina la extensión del nombre ya que es implícita al cargarlos
		for(int i= 0; i < circuitos.length; i++) {
			circuitos[i] = circuitos[i].substring(0, circuitos[i].lastIndexOf("."));
		}
		//Si no se pasan los items en forma de ComboBoxModel el WindowBuilder no lo reconoce
		return new JComboBox<String>(new DefaultComboBoxModel<String>(circuitos));
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

	public JComboBox<String> getcBoxCircuito() {
		return cBoxCircuito;
	}
	
	 
}
