package vista;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

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
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

/**
 * Ventana de Swing que actuará como interfaz de usuario con la que se interactúa con el
 * programa a través de sus controles y que muestra todo tipo de información sobre el proceso
 * evolutivo que se lleva a cabo a lo largo de su ejecución.
 * @author Alberto
 */
public class PanelControl extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/* Componentes de la interfaz que deben de poder ser accesibles por otras clases
	 * desde el controlador, para poder actualizar y manipularla */
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
	private JButton btnSalir;
	private JPanel panelDatosEntidad;
	private JComboBox<String> cBoxCircuito;
	
	/**
	 * Mapa que contiene los labels cuya información debe de poder ser actualizada
	 * por el modelo a través del Visualizador cuando se modifiquen los datos.
	 * Cada label tiene su identificador que establecemos con setName() y que
	 * utilizamos como clave textual de su entrada del mapa. */
	private HashMap<String, JLabel> mapaLabels;
	
	/**
	 * El controlador con el que se comunica con el resto de clases del programa
	 */
	private Controlador controlador;
	
	/**
	 * Inicializa el panel de control, construyendo a su vez todos sus contenedores y componentes 
	 * que conforman la interfaz de usuario siguiendo una estructura de capas que define la
	 * disposición que vemos en pantalla y la estética que tendrá
	 * @param controlador que se le pasa para conectarlo con el resto del programa */
	public PanelControl(Controlador controlador) {
		
		this.controlador = controlador;
		
		mapaLabels = new HashMap<String, JLabel>();
		
		//Inicia los atributos del frame del panel de control
        this.setLocation(new Point(0, 0));
        this.setName("frameCircuitoGenetico");
        this.setTitle("Circuito Gen\u00E9tico");
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(PanelControl.class.getResource("/img/gene_icon.png")));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        
        initPanelPrincipal();
        
        /* Cuando se hayan creado todos los componentes dentro del frame, ajusta el tamaño de la ventana al necesario
         * para que quepan todos los elementos
         */
        this.pack();
        //Solo queremos centrarlo verticalmente, que quede alineado a la izquierda de la pantalla
        centrarFrameVerticalmente(); 
        this.setVisible(true); //Muestra el frame en la pantalla
	}

	/**
	 * Inicia el panel principal que contendrá todos los componentes del frame
	 */
	private void initPanelPrincipal() {
		
		//Panel que hace de fondo de la interfaz, el contenedor principal
		
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panelPrincipal.setBackground(new Color(46, 10, 84));
        panelPrincipal.setLayout(new BorderLayout(0, 0));
        this.setContentPane(panelPrincipal);
        
        //Panel que hace de encabezado y en el que se muestra el título
        
        JPanel panelTop = new JPanel();
        panelTop.setOpaque(false);
        panelPrincipal.add(panelTop, BorderLayout.NORTH);
        
        JLabel lblTitulo = new JLabel("Panel de Control");
        lblTitulo.setMinimumSize(new Dimension(92, 18));
        lblTitulo.setMaximumSize(new Dimension(148, 30));
        lblTitulo.setPreferredSize(new Dimension(148, 25));
        lblTitulo.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblTitulo.setVerticalAlignment(SwingConstants.BOTTOM);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(255, 255, 255));
        lblTitulo.setFont(new Font("Lato", Font.BOLD, 18));
        panelTop.add(lblTitulo);
        
        //Bordes alrededor del contenido de la interfaz
        
        JPanel bordeIzq = new JPanel();
        bordeIzq.setPreferredSize(new Dimension(15, 10));
        bordeIzq.setOpaque(false);
        panelPrincipal.add(bordeIzq, BorderLayout.WEST);
        
        JPanel bordeDrc = new JPanel();
        bordeDrc.setPreferredSize(new Dimension(15, 10));
        bordeDrc.setOpaque(false);
        panelPrincipal.add(bordeDrc, BorderLayout.EAST);

        JPanel bordeAbajo = new JPanel();
        bordeAbajo.setPreferredSize(new Dimension(10, 15));
        bordeAbajo.setOpaque(false);
        panelPrincipal.add(bordeAbajo, BorderLayout.SOUTH);
        
        initPanelContenido(panelPrincipal);
	}

	/**
	 * Inicia el panel en el que se muestra todo el contenido interactivo de la interfaz.
	 * Este panel contendrá tres secciones distintas que agrupan componentes relacionados entre sí
	 * @param panelPrincipal
	 */
	private void initPanelContenido(JPanel panelPrincipal) {
		
		//Panel que contiene las tres secciones
		
		JPanel panelContenido = new JPanel();
        panelContenido.setOpaque(false);
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        
        //Inicia cada una de las secciones y les añade espacio entre ellas
        
        initPanelControles(panelContenido);
 
        //Separador entre secciones
        Component separador1 = Box.createVerticalStrut(10);
        panelContenido.add(separador1);
        
        initPanelConfiguracion(panelContenido);

        Component separador2 = Box.createVerticalStrut(10);
        panelContenido.add(separador2);
        
        initPanelMonitorizacion(panelContenido);
	}

	/**
	 * Inicia el panel que contiene todos los botones para interaccionar con el flujo de ejecución del programa
	 * y donde se muestra el número de generaciones
	 * @param panelContenido
	 */
	private void initPanelControles(JPanel panelContenido) {

		//Primera sección
		
		JPanel panelControles = new JPanel();
		panelControles.setMaximumSize(new Dimension(32767, 150));
		panelControles.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Controles", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
		panelControles.setBackground(new Color(99, 9, 177));
		panelControles.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
		panelContenido.add(panelControles);

		//Se encapsula cada componente en su respectivo panel para facilitar la disposición de la interfaz
		
		JPanel panelGeneracion = new JPanel();
		panelGeneracion.setPreferredSize(new Dimension(10, 25));
		panelGeneracion.setMinimumSize(new Dimension(10, 25));
		panelGeneracion.setMaximumSize(new Dimension(32767, 25));
		panelGeneracion.setOpaque(false);
		panelGeneracion.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelGeneracion.setLayout(new FlowLayout());
		panelControles.add(panelGeneracion);

		//Label que muestra el número de generaciones
		
		lblGeneracion = new JLabel("Generación: 0");
		lblGeneracion.setVerticalAlignment(SwingConstants.TOP);
		lblGeneracion.setForeground(new Color(255, 255, 255));
		lblGeneracion.setFont(new Font("Lato", Font.BOLD, 16));
		lblGeneracion.setAlignmentX(Component.CENTER_ALIGNMENT);
		/* Todos los labels cuyo valor deba actualizarse desde el controlador deben declarar un
		 * nombre que haga de identificador. El label se introducirá en el mapa de labels, siendo
		 * su clave el identificador que hemos establecido y el valor el propio label. De esta 
		 * forma se puede accedera él a través de su nombre, lo que posibilita no tener que
		 * conocer el "campo" que se debe actualizar desde otras clases y que se pueda generalizar
		 * todas las actualizaciones de la interfaz en ún solo método
		 */
		lblGeneracion.setName("Generacion");
		mapaLabels.put(lblGeneracion.getName(), lblGeneracion);
		panelGeneracion.add(lblGeneracion);

		JPanel panelModoAutomatico = new JPanel();
		panelModoAutomatico.setMinimumSize(new Dimension(10, 28));
		panelModoAutomatico.setPreferredSize(new Dimension(10, 25));
		panelModoAutomatico.setMaximumSize(new Dimension(32767, 28));
		panelModoAutomatico.setOpaque(false);
		panelModoAutomatico.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelModoAutomatico.setLayout(new FlowLayout());
		panelControles.add(panelModoAutomatico);
		
		//Checkbox que activa o desactiva el modo automático
		
		cbModoAutomatico = new JCheckBox("Modo Automático");
		cbModoAutomatico.setOpaque(false);
		cbModoAutomatico.setVerticalTextPosition(SwingConstants.BOTTOM);
		cbModoAutomatico.setPreferredSize(new Dimension(140, 21));
		cbModoAutomatico.setHorizontalAlignment(SwingConstants.TRAILING);
		cbModoAutomatico.setHorizontalTextPosition(SwingConstants.LEADING);
		cbModoAutomatico.setForeground(new Color(255, 255, 255));
		cbModoAutomatico.setFont(new Font("Lato", Font.BOLD, 14));
		cbModoAutomatico.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cbModoAutomatico.setToolTipText("");
		cbModoAutomatico.setFocusable(false);
		panelModoAutomatico.add(cbModoAutomatico);

		JPanel panelProceder = new JPanel();
		panelProceder.setMinimumSize(new Dimension(10, 35));
		panelProceder.setPreferredSize(new Dimension(0, 35));
		panelProceder.setMaximumSize(new Dimension(32767, 35));
		panelProceder.setOpaque(false);
		panelProceder.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelProceder.setLayout(new FlowLayout());
		panelControles.add(panelProceder);
		
		/* Botón que al inicio del programa muestra el texto "Empezar" y comienza el proceso.
		 * Después de eso se transoforma a "Siguiente" y su función es continuar el proceso 
		 * cuando termina cada generación (si no está el modo automático activado).
		 * Una vez se reincie el proceso, vuelve a actuar como botón de empezar.
		 */
		
		btnProceder = new JButton("Empezar");
		btnProceder.setVerticalAlignment(SwingConstants.TOP);
		btnProceder.setPreferredSize(new Dimension(200, 28));
		btnProceder.setFont(new Font("Lato", Font.BOLD, 16));
		btnProceder.setBackground(new Color(141, 71, 201));
		btnProceder.setForeground(new Color(255, 255, 255));
		btnProceder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnProceder.setToolTipText("Comienza una nueva generaci\u00F3n ");
		btnProceder.setFocusable(false);
		panelProceder.add(btnProceder);

		JPanel panelPausar = new JPanel();
		panelPausar.setMinimumSize(new Dimension(10, 35));
		panelPausar.setPreferredSize(new Dimension(0, 35));
		panelPausar.setMaximumSize(new Dimension(32767, 35));
		panelPausar.setOpaque(false);
		panelPausar.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelPausar.setLayout(new FlowLayout());
		panelControles.add(panelPausar);
		
		//Botón que pausa o reanuda el proceso según esté parado o no
		
		btnPausar = new JButton("Pausar");
		btnPausar.setVerticalAlignment(SwingConstants.TOP);
		btnPausar.setPreferredSize(new Dimension(200, 28));
		btnPausar.setFont(new Font("Lato", Font.BOLD, 16));
		btnPausar.setBackground(new Color(141, 71, 201));
		btnPausar.setForeground(new Color(255, 255, 255));
		btnPausar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnPausar.setToolTipText("Pausa/Reanuda el movimiento de las entidades");
		btnPausar.setFocusable(false);
		btnPausar.setEnabled(false);
		panelPausar.add(btnPausar);

		JPanel panelReiniciar = new JPanel();
		panelReiniciar.setMinimumSize(new Dimension(10, 35));
		panelReiniciar.setPreferredSize(new Dimension(0, 35));
		panelReiniciar.setMaximumSize(new Dimension(32767, 35));
		panelReiniciar.setOpaque(false);
		panelReiniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelReiniciar.setLayout(new FlowLayout());
		panelControles.add(panelReiniciar);

		//Botón que reinicia el proceso y devuelve al programa a su estado inicial
		
		btnReiniciar = new JButton("Reiniciar");
		btnReiniciar.setVerticalAlignment(SwingConstants.TOP);
		btnReiniciar.setPreferredSize(new Dimension(200, 28));
		btnReiniciar.setFont(new Font("Lato", Font.BOLD, 16));
		btnReiniciar.setBackground(new Color(141, 71, 201));
		btnReiniciar.setForeground(new Color(255, 255, 255));
		btnReiniciar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnReiniciar.setToolTipText("Termina de ejecutar el proceso evolutivo y vuelve al estado inicial");
		btnReiniciar.setFocusable(false);
		btnReiniciar.setEnabled(false);
		panelReiniciar.add(btnReiniciar);

		JPanel panelSalir = new JPanel();
		panelSalir.setMinimumSize(new Dimension(10, 40));
		panelSalir.setPreferredSize(new Dimension(0, 35));
		panelSalir.setMaximumSize(new Dimension(32767, 35));
		panelSalir.setOpaque(false);
		panelSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelSalir.setLayout(new FlowLayout());
		panelControles.add(panelSalir);

		//Botón que cierra el programa
		
		btnSalir = new JButton("Salir");
		btnSalir.setVerticalAlignment(SwingConstants.TOP);
		btnSalir.setPreferredSize(new Dimension(200, 28));
		btnSalir.setFont(new Font("Lato", Font.BOLD, 16));
		btnSalir.setBackground(new Color(141, 71, 201));
		btnSalir.setForeground(new Color(255, 255, 255));
		btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSalir.setToolTipText("Cierra el programa");
		btnSalir.setFocusable(false);
		panelSalir.add(btnSalir);
	}
	
	/**
	 * Inicia el panel que contiene inputs para que el usuario pueda alterar el proceso, modificando
	 * valores de parámetros de la población o cambiando de circuito
	 * @param panelContenido
	 */
	private void initPanelConfiguracion(JPanel panelContenido) {
		
		//Segunda seccion
		
        JPanel panelConfig = new JPanel();
        panelConfig.setMaximumSize(new Dimension(350, 32767));
        panelConfig.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Configuraci\u00F3n", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
        panelConfig.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelConfig.setBackground(new Color(99, 9, 177));
        panelConfig.setLayout(new BoxLayout(panelConfig, BoxLayout.Y_AXIS));
        panelContenido.add(panelConfig);
        
        JPanel panelParametros = new JPanel();
        panelParametros.setOpaque(false);
        panelParametros.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelParametros.setLayout(new BoxLayout(panelParametros, BoxLayout.Y_AXIS));
        panelConfig.add(panelParametros);
        
        JPanel panelCircuito = new JPanel();
        panelCircuito.setMaximumSize(new Dimension(235, 32767));
        panelCircuito.setOpaque(false);
        panelCircuito.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCircuito.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelParametros.add(panelCircuito);
        
        JLabel lblCircuito = new JLabel("Circuito");
        lblCircuito.setForeground(new Color(255, 255, 255));
        lblCircuito.setFont(new Font("Lato", Font.BOLD, 14));
        lblCircuito.setLabelFor(cBoxCircuito);
        panelCircuito.add(lblCircuito);
        
        /* Combo box que abre una lista de circuitos seleccionables para el proceso.
         * Rellena la lista obteniendo los circuitos de los ficheros de recursos 
         * contenidos en el proyecto. Se cargarán en el modelo y la ventana cuando
         * se cambie el que está seleccionado
         */
        
        cBoxCircuito = rellenarCboxCircuito();
        cBoxCircuito.setPreferredSize(new Dimension(164, 26));
        cBoxCircuito.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cBoxCircuito.setFont(new Font("Lato", Font.BOLD, 12));
        cBoxCircuito.setToolTipText("Selecciona un circuito para las entidades ");
        cBoxCircuito.setSelectedIndex(0);
        cBoxCircuito.setMaximumRowCount(10);
        cBoxCircuito.setEditable(true);
        panelCircuito.add(cBoxCircuito);
        
        JPanel panelPoblacionTotal = new JPanel();
        panelPoblacionTotal.setMaximumSize(new Dimension(235, 32767));
        panelPoblacionTotal.setOpaque(false);
        panelPoblacionTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPoblacionTotal.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelParametros.add(panelPoblacionTotal);
        
        /* Spinners que permiten introducir un valor para modificar un determinado parámetro
         * del proceso. Se pueden incrementar o decrementar en intervalos especificados y dentro
         * de unos límites inferiores y superiores.
         */    
        
        JLabel lblPoblacionTotal = new JLabel("Población total");
        lblPoblacionTotal.setForeground(new Color(255, 255, 255));
        lblPoblacionTotal.setFont(new Font("Lato", Font.BOLD, 14));
        panelPoblacionTotal.add(lblPoblacionTotal);
        
        //Spinner que permite modificar el número de entidades de la población.
        
        spPoblacion = new JSpinner(new SpinnerNumberModel(1000, 4, 15000, 50));
        spPoblacion.setPreferredSize(new Dimension(72, 28));
        lblPoblacionTotal.setLabelFor(spPoblacion);
        spPoblacion.setFont(new Font("Lato", Font.BOLD, 12));
        spPoblacion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spPoblacion.setToolTipText("Modifica el n\u00FAmero de entidades que se generan");
        spPoblacion.setName("NumEntidades");
        panelPoblacionTotal.add(spPoblacion);

        JPanel panelTasaMutacion = new JPanel();
        panelTasaMutacion.setMaximumSize(new Dimension(235, 32767));
        panelTasaMutacion.setOpaque(false);
        panelTasaMutacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTasaMutacion.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelParametros.add(panelTasaMutacion);
        
        JLabel lblTasaMutacion = new JLabel("Tasa de mutación (%)");
        lblTasaMutacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTasaMutacion.setForeground(new Color(255, 255, 255));
        lblTasaMutacion.setFont(new Font("Lato", Font.BOLD, 14));
        panelTasaMutacion.add(lblTasaMutacion);
        
        //Spinner que permite modificar el número de entidades de la población.
        
        spMutacion = new JSpinner(new SpinnerNumberModel(20, 0, 100, 1));
        spMutacion.setPreferredSize(new Dimension(56, 28));
        lblTasaMutacion.setLabelFor(spMutacion);
        spMutacion.setFont(new Font("Lato", Font.BOLD, 12));
        spMutacion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spMutacion.setToolTipText("Modifica la probabilidad de que muten los genes");
        spMutacion.setName("TasaMutacion");
        panelTasaMutacion.add(spMutacion);
        
        JPanel panelTiempoObjetivo = new JPanel();
        panelTiempoObjetivo.setMaximumSize(new Dimension(235, 32767));
        panelTiempoObjetivo.setOpaque(false);
        panelTiempoObjetivo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTiempoObjetivo.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelParametros.add(panelTiempoObjetivo);
        
        JLabel lblTiempoObjetivo = new JLabel("Tiempo objetivo (frames)");
        lblTiempoObjetivo.setForeground(new Color(255, 255, 255));
        lblTiempoObjetivo.setFont(new Font("Lato", Font.BOLD, 14));
        panelTiempoObjetivo.add(lblTiempoObjetivo);
        
        //Spinner que permite modificar el objetivo de tiempo que deben alcanzar las entidades
        
        spTiempoObjetivo = new JSpinner(new SpinnerNumberModel(140, 2, 2000, 1));
        spTiempoObjetivo.setPreferredSize(new Dimension(64, 28));
        lblTiempoObjetivo.setLabelFor(spTiempoObjetivo);
        spTiempoObjetivo.setFont(new Font("Lato", Font.BOLD, 12));
        spTiempoObjetivo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spTiempoObjetivo.setToolTipText("Modifica el tiempo en frames que deben lograr las entidades para llegar a la meta");
        spTiempoObjetivo.setName("TiempoObjetivo");
        panelTiempoObjetivo.add(spTiempoObjetivo);

        JPanel panelTiempoVida = new JPanel();
        panelTiempoVida.setMaximumSize(new Dimension(235, 32767));
        panelTiempoVida.setMinimumSize(new Dimension(10, 40));
        panelTiempoVida.setOpaque(false);
        panelTiempoVida.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTiempoVida.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelParametros.add(panelTiempoVida);
        
        JLabel lblTiempoVida = new JLabel("Tiempo de vida (frames)");
        lblTiempoVida.setForeground(new Color(255, 255, 255));
        lblTiempoVida.setFont(new Font("Lato", Font.BOLD, 14));
        panelTiempoVida.add(lblTiempoVida);
        
        //Spinner que permite modificar el tiempo de vida de las entidades en frames
        
        spTiempoVida = new JSpinner(new SpinnerNumberModel(400, 10, 2001, 5));
        spTiempoVida.setPreferredSize(new Dimension(64, 28));
        lblTiempoVida.setLabelFor(spTiempoVida);
        spTiempoVida.setFont(new Font("Lato", Font.BOLD, 12));
        spTiempoVida.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spTiempoVida.setToolTipText("Modifica el tiempo en frames que viven las entidades");
        spTiempoVida.setName("TiempoVida");
        panelTiempoVida.add(spTiempoVida);
	}
	
	/**
	 * Inicia el panel que contiene información en tiempo real sobre el proceso evolutivo, tanto en
	 * total como cada generación. También muestra los datos de una entidad que esté siendo 
	 * monitorizada
	 * @param panelContenido
	 */
	private void initPanelMonitorizacion(JPanel panelContenido) {
		JPanel panelMonitorizacion = new JPanel();
        panelMonitorizacion.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Monitorizaci\u00F3n", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
        panelMonitorizacion.setBackground(new Color(99, 9, 177));
        panelMonitorizacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelMonitorizacion.setLayout(new BoxLayout(panelMonitorizacion, BoxLayout.Y_AXIS));
        panelContenido.add(panelMonitorizacion);
        
        JPanel panelDatosGeneracion = new JPanel();
        panelDatosGeneracion.setBackground(new Color(99, 9, 177));
        panelDatosGeneracion.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panelDatosGeneracion.setOpaque(false);
        panelDatosGeneracion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelDatosGeneracion.setLayout(new BoxLayout(panelDatosGeneracion, BoxLayout.X_AXIS));
        panelMonitorizacion.add(panelDatosGeneracion);
        
        JPanel panelTotalGeneraciones = new JPanel();
        panelTotalGeneraciones.setBorder(new LineBorder(new Color(169, 58, 222)));
        panelTotalGeneraciones.setOpaque(false);
        panelTotalGeneraciones.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelTotalGeneraciones.setLayout(new BorderLayout());
        panelDatosGeneracion.add(panelTotalGeneraciones);
        
        //Inicia cada sección donde se monitorizan datos distintos
        
        initDatosTotales(panelTotalGeneraciones);

        initDatosGeneracionActual(panelDatosGeneracion);
        
        initDatosEntidad(panelMonitorizacion);
	}

	/**
	 * Inicia el panel que muestra información sobre el proceso evolutivo a lo largo de
	 * todas las generaciones hasta el momento
	 * @param panelTotalGeneraciones
	 */
	private void initDatosTotales(JPanel panelTotalGeneraciones) {
		
		JPanel panelTotalGeneracionesTitulo = new JPanel();
        panelTotalGeneracionesTitulo.setBorder(new LineBorder(new Color(154, 18, 218)));
        panelTotalGeneracionesTitulo.setOpaque(false);
        panelTotalGeneracionesTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTotalGeneracionesTitulo.setLayout(new BoxLayout(panelTotalGeneracionesTitulo, BoxLayout.Y_AXIS));
        panelTotalGeneraciones.add(panelTotalGeneracionesTitulo, BorderLayout.NORTH);
        
        JLabel lblDatosTotalGeneraciones = new JLabel("Datos Totales");
        lblDatosTotalGeneraciones.setVerticalAlignment(SwingConstants.BOTTOM);
        lblDatosTotalGeneraciones.setHorizontalAlignment(SwingConstants.CENTER);
        lblDatosTotalGeneraciones.setPreferredSize(new Dimension(78, 22));
        lblDatosTotalGeneraciones.setMinimumSize(new Dimension(77, 8));
        lblDatosTotalGeneraciones.setMaximumSize(new Dimension(85, 22));
        lblDatosTotalGeneraciones.setForeground(new Color(255, 255, 255));
        lblDatosTotalGeneraciones.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDatosTotalGeneraciones.setFont(new Font("Lato", Font.BOLD, 14));
        panelTotalGeneracionesTitulo.add(lblDatosTotalGeneraciones);
        
        JPanel panelTotalGeneracionesContenido = new JPanel();
        panelTotalGeneracionesContenido.setBorder(new LineBorder(new Color(154, 18, 218)));
        panelTotalGeneracionesContenido.setOpaque(false);
        panelTotalGeneracionesContenido.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelTotalGeneracionesContenido.setLayout(new BoxLayout(panelTotalGeneracionesContenido,BoxLayout.Y_AXIS));
        panelTotalGeneraciones.add(panelTotalGeneracionesContenido, BorderLayout.CENTER);
        
        //Muestra el mejor tiempo obtenido por alguna entidad hasta el momento
        
        lblTiempoRecord = new JLabel("Tiempo record: 0");
        lblTiempoRecord.setBorder(new EmptyBorder(0, 3, 0, 3));
        lblTiempoRecord.setPreferredSize(new Dimension(93, 20));
        lblTiempoRecord.setMaximumSize(new Dimension(140, 20));
        lblTiempoRecord.setForeground(new Color(255, 255, 255));
        lblTiempoRecord.setFont(new Font("Lato", Font.BOLD, 12));
        lblTiempoRecord.setName("TiempoRecord");
        mapaLabels.put(lblTiempoRecord.getName(), lblTiempoRecord);
        panelTotalGeneracionesContenido.add(lblTiempoRecord);
        
        //Muestra la mejor aptitud obtenida por alguna entidad hasta el momento
        
        lblMejorAptitud = new JLabel("Mejor aptitud: 0");
        lblMejorAptitud.setBorder(new EmptyBorder(0, 3, 0, 3));
        lblMejorAptitud.setPreferredSize(new Dimension(140, 20));
        lblMejorAptitud.setMaximumSize(new Dimension(140, 20));
        lblMejorAptitud.setForeground(new Color(255, 255, 255));
        lblMejorAptitud.setFont(new Font("Lato", Font.BOLD, 12));
        lblMejorAptitud.setName("MejorAptitud");
        mapaLabels.put(lblMejorAptitud.getName(), lblMejorAptitud);
        panelTotalGeneracionesContenido.add(lblMejorAptitud);
        
        //Muestra el recuento total de veces que las entidades han llegado a la meta
        
        lblMetas = new JLabel("Metas alcanzadas: 0");
        lblMetas.setBorder(new EmptyBorder(0, 3, 0, 3));
        lblMetas.setMaximumSize(new Dimension(140, 20));
        lblMetas.setPreferredSize(new Dimension(112, 20));
        lblMetas.setForeground(new Color(255, 255, 255));
        lblMetas.setFont(new Font("Lato", Font.BOLD, 12));
        lblMetas.setName("Metas");
        mapaLabels.put(lblMetas.getName(), lblMetas);
        panelTotalGeneracionesContenido.add(lblMetas);
        
        //Muestra el recuento total de veces que las entidades han chocado con obstáculos
        
        lblColisiones = new JLabel("Colisiones: 0");
        lblColisiones.setBorder(new EmptyBorder(0, 3, 0, 3));
        lblColisiones.setPreferredSize(new Dimension(73, 20));
        lblColisiones.setMaximumSize(new Dimension(140, 20));
        lblColisiones.setForeground(new Color(255, 255, 255));
        lblColisiones.setFont(new Font("Lato", Font.BOLD, 12));
        lblColisiones.setName("Colisiones");
        mapaLabels.put(lblColisiones.getName(), lblColisiones);
        panelTotalGeneracionesContenido.add(lblColisiones);
	}
	
	/**
	 * Inicia el panel que muestra información sobre la generación actual del proceso
	 * @param panelDatosGeneracion
	 */
	private void initDatosGeneracionActual(JPanel panelDatosGeneracion) {
		JPanel panelGeneracionActual = new JPanel();
        panelGeneracionActual.setBorder(new LineBorder(new Color(169, 58, 222)));
        panelGeneracionActual.setOpaque(false);
        panelGeneracionActual.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelGeneracionActual.setLayout(new BorderLayout());
        panelDatosGeneracion.add(panelGeneracionActual);
        
        JPanel panelGeneracionActualTitulo = new JPanel();
        panelGeneracionActualTitulo.setBorder(new LineBorder(new Color(154, 18, 218)));
        panelGeneracionActualTitulo.setOpaque(false);
        panelGeneracionActualTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelGeneracionActualTitulo.setLayout(new BoxLayout(panelGeneracionActualTitulo, BoxLayout.Y_AXIS));
        panelGeneracionActual.add(panelGeneracionActualTitulo, BorderLayout.NORTH);
        
        JLabel lblDatosGeneracionActual = new JLabel("Datos Generación");
        lblDatosGeneracionActual.setVerticalAlignment(SwingConstants.BOTTOM);
        lblDatosGeneracionActual.setHorizontalAlignment(SwingConstants.CENTER);
        lblDatosGeneracionActual.setPreferredSize(new Dimension(130, 22));
        lblDatosGeneracionActual.setMaximumSize(new Dimension(140, 22));
        lblDatosGeneracionActual.setForeground(new Color(255, 255, 255));
        lblDatosGeneracionActual.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDatosGeneracionActual.setFont(new Font("Lato", Font.BOLD, 14));
        panelGeneracionActualTitulo.add(lblDatosGeneracionActual);
        
        JPanel panelGeneracionActualContenido = new JPanel();
        panelGeneracionActualContenido.setBorder(new LineBorder(new Color(154, 18, 218)));
        panelGeneracionActualContenido.setOpaque(false);
        panelGeneracionActualContenido.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelGeneracionActualContenido.setLayout(new BoxLayout(panelGeneracionActualContenido,BoxLayout.Y_AXIS));
        panelGeneracionActual.add(panelGeneracionActualContenido, BorderLayout.CENTER);
        
        //Muestra el mejor tiempo obtenido por alguna entidad en la generación actual
        
        lblTiempoRecordActual = new JLabel("Tiempo record: 0");
        lblTiempoRecordActual.setBorder(new EmptyBorder(0, 3, 0, 3));
        lblTiempoRecordActual.setMaximumSize(new Dimension(140, 20));
        lblTiempoRecordActual.setPreferredSize(new Dimension(93, 20));
        lblTiempoRecordActual.setForeground(new Color(255, 255, 255));
        lblTiempoRecordActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblTiempoRecordActual.setName("TiempoRecordActual");
        mapaLabels.put(lblTiempoRecordActual.getName(), lblTiempoRecordActual);
        panelGeneracionActualContenido.add(lblTiempoRecordActual);
        
        //Muestra la mejor aptitud obtenida por alguna entidad en la generación actual
        
        lblMejorAptitudActual = new JLabel("Mejor aptitud: 0");
        lblMejorAptitudActual.setBorder(new EmptyBorder(0, 3, 0, 3));
        lblMejorAptitudActual.setPreferredSize(new Dimension(140, 20));
        lblMejorAptitudActual.setMaximumSize(new Dimension(150, 20));
        lblMejorAptitudActual.setForeground(new Color(255, 255, 255));
        lblMejorAptitudActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblMejorAptitudActual.setName("MejorAptitudActual");
        mapaLabels.put(lblMejorAptitudActual.getName(), lblMejorAptitudActual);
        panelGeneracionActualContenido.add(lblMejorAptitudActual);
        
        /* Muestra el recuento de veces que las entidades han llegado a la meta
         * en la generación actual */
        
        lblMetasActual = new JLabel("Metas alcanzadas: 0");
        lblMetasActual.setBorder(new EmptyBorder(0, 3, 0, 3));
        lblMetasActual.setPreferredSize(new Dimension(112, 20));
        lblMetasActual.setMaximumSize(new Dimension(140, 20));
        lblMetasActual.setForeground(new Color(255, 255, 255));
        lblMetasActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblMetasActual.setName("MetasActual");
        mapaLabels.put(lblMetasActual.getName(), lblMetasActual);
        panelGeneracionActualContenido.add(lblMetasActual);
        
        /* Muestra el recuento de veces que las entidades han chocado con obstáculo
         * en la generación actual */
        
        lblColisionesActual = new JLabel("Colisiones: 0");
        lblColisionesActual.setBorder(new EmptyBorder(0, 3, 0, 3));
        lblColisionesActual.setPreferredSize(new Dimension(73, 20));
        lblColisionesActual.setMaximumSize(new Dimension(140, 20));
        lblColisionesActual.setForeground(new Color(255, 255, 255));
        lblColisionesActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblColisionesActual.setName("ColisionesActual");
        mapaLabels.put(lblColisionesActual.getName(), lblColisionesActual);
        panelGeneracionActualContenido.add(lblColisionesActual);
	}
	
	/**
	 * Inicia el panel que muestra información sobre una entidad que esté siendo monitorizada
	 * en ese momento tras seleccionarla en la venta, si es que hay alguna
	 * @param panelMonitorizacion
	 */
	private void initDatosEntidad(JPanel panelMonitorizacion) {
		panelDatosEntidad = new JPanel();
        panelDatosEntidad.setMaximumSize(new Dimension(350, 32767));
        panelDatosEntidad.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panelDatosEntidad.setOpaque(true);
        panelDatosEntidad.setBackground(new Color(99, 9, 177));
        panelDatosEntidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelDatosEntidad.setLayout(new BorderLayout());
        panelMonitorizacion.add(panelDatosEntidad);
        
        JPanel panelDatosEntidadTitulo = new JPanel();
        panelDatosEntidadTitulo.setMaximumSize(new Dimension(350, 32767));
        panelDatosEntidadTitulo.setBorder(new LineBorder(new Color(169, 58, 222)));
        panelDatosEntidadTitulo.setOpaque(false);
        panelDatosEntidadTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelDatosEntidadTitulo.setLayout(new BoxLayout(panelDatosEntidadTitulo, BoxLayout.Y_AXIS));
        panelDatosEntidad.add(panelDatosEntidadTitulo, BorderLayout.NORTH);
        
        /* Muestra el número identificador de la entidad que esta siendo actualmente monitorizada,
         * es decir, su índice dentro de la población*/
        
        lblEntidad = new JLabel("Entidad: -", SwingConstants.CENTER);
        lblEntidad.setVerticalAlignment(SwingConstants.BOTTOM);
        lblEntidad.setMinimumSize(new Dimension(52, 20));
        lblEntidad.setMaximumSize(new Dimension(360, 25));
        lblEntidad.setPreferredSize(new Dimension(52, 22));
        lblEntidad.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblEntidad.setForeground(new Color(255, 255, 255));
        lblEntidad.setFont(new Font("Lato", Font.BOLD, 14));
        lblEntidad.setName("Entidad");
        lblEntidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        mapaLabels.put(lblEntidad.getName(), lblEntidad);
        panelDatosEntidadTitulo.add(lblEntidad);
        
        JPanel panelDatosEntidadContenido = new JPanel();
        panelDatosEntidadContenido.setBorder(new LineBorder(new Color(169, 58, 222)));
        panelDatosEntidadContenido.setOpaque(false);
        panelDatosEntidadContenido.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelDatosEntidadContenido.setLayout(new BoxLayout(panelDatosEntidadContenido, BoxLayout.Y_AXIS));
        panelDatosEntidad.add(panelDatosEntidadContenido, BorderLayout.CENTER);
        
        JPanel panelParamsEntidad = new JPanel();
        panelParamsEntidad.setOpaque(false);
        panelParamsEntidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelParamsEntidad.setLayout(new BoxLayout(panelParamsEntidad, BoxLayout.Y_AXIS));
        panelDatosEntidadContenido.add(panelParamsEntidad);
        
        //Muestra la posición actual de la entidad en el eje de coordenadas de la ventana 
        
        lblPosicion = new JLabel("Posicion: (0,0)");
        lblPosicion.setPreferredSize(new Dimension(102, 20));
        lblPosicion.setMinimumSize(new Dimension(102, 20));
        lblPosicion.setMaximumSize(new Dimension(180, 20));
        lblPosicion.setForeground(new Color(255, 255, 255));
        lblPosicion.setFont(new Font("Lato", Font.BOLD, 12));
        lblPosicion.setName("Posicion");
        mapaLabels.put(lblPosicion.getName(), lblPosicion);
        panelParamsEntidad.add(lblPosicion);
        
        //Muestra la velocidad actual de la entidad en el eje de coordenadas de la ventana 
        
        lblVelocidad = new JLabel("Velocidad: (0,0)");
        lblVelocidad.setMaximumSize(new Dimension(180, 20));
        lblVelocidad.setMinimumSize(new Dimension(108, 20));
        lblVelocidad.setPreferredSize(new Dimension(108, 20));
        lblVelocidad.setForeground(new Color(255, 255, 255));
        lblVelocidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblVelocidad.setName("Velocidad");
        mapaLabels.put(lblVelocidad.getName(), lblVelocidad);
        panelParamsEntidad.add(lblVelocidad);
        
        //Muestra la aceleración actual de la entidad en el eje de coordenadas de la ventana 
        
        lblAceleracion = new JLabel("Aceleración: (0,0)");
        lblAceleracion.setMinimumSize(new Dimension(118, 20));
        lblAceleracion.setMaximumSize(new Dimension(180, 20));
        lblAceleracion.setPreferredSize(new Dimension(118, 20));
        lblAceleracion.setForeground(new Color(255, 255, 255));
        lblAceleracion.setFont(new Font("Lato", Font.BOLD, 12));
        lblAceleracion.setName("Aceleracion");
        mapaLabels.put(lblAceleracion.getName(), lblAceleracion);
        panelParamsEntidad.add(lblAceleracion);
        
        //Muestra la distancia actual a la que se encuentra la entidad sobre la meta 
        
        lblDistancia = new JLabel("Distancia: 0");
        lblDistancia.setMaximumSize(new Dimension(120, 20));
        lblDistancia.setMinimumSize(new Dimension(65, 20));
        lblDistancia.setPreferredSize(new Dimension(65, 20));
        lblDistancia.setForeground(new Color(255, 255, 255));
        lblDistancia.setFont(new Font("Lato", Font.BOLD, 12));
        lblDistancia.setName("DistanciaEntidad");
        mapaLabels.put(lblDistancia.getName(), lblDistancia);
        panelParamsEntidad.add(lblDistancia);
        
        //Muestra la distancia más cercana a la que ha estado la entidad sobre la meta 
        
        lblDistMinEntidad = new JLabel("Distancia mínima: 0");
        lblDistMinEntidad.setMaximumSize(new Dimension(180, 20));
        lblDistMinEntidad.setMinimumSize(new Dimension(110, 20));
        lblDistMinEntidad.setPreferredSize(new Dimension(110, 20));
        lblDistMinEntidad.setForeground(new Color(255, 255, 255));
        lblDistMinEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblDistMinEntidad.setName("DistanciaMinEntidad");
        mapaLabels.put(lblDistMinEntidad.getName(), lblDistMinEntidad);
        panelParamsEntidad.add(lblDistMinEntidad);
        
        //Muestra en qué estado se encuentra la entidad (activa, llegado, chocado)
        
        lblEstado = new JLabel("Estado: -");
        lblEstado.setMaximumSize(new Dimension(100, 20));
        lblEstado.setMinimumSize(new Dimension(49, 20));
        lblEstado.setPreferredSize(new Dimension(49, 20));
        lblEstado.setForeground(new Color(255, 255, 255));
        lblEstado.setFont(new Font("Lato", Font.BOLD, 12));
        lblEstado.setName("EstadoEntidad");
        mapaLabels.put(lblEstado.getName(), lblEstado);
        panelParamsEntidad.add(lblEstado);
        
        /* Muestra el tiempo en llegar a la meta que ha obtenido la entidad al acabar 
         * su ciclo de vida, que de no haberlo conseguido, será su tiempo de vida */
        
        lblTiempoEntidad = new JLabel("Tiempo obtenido: 0");
        lblTiempoEntidad.setMinimumSize(new Dimension(106, 20));
        lblTiempoEntidad.setMaximumSize(new Dimension(130, 20));
        lblTiempoEntidad.setPreferredSize(new Dimension(106, 20));
        lblTiempoEntidad.setForeground(new Color(255, 255, 255));
        lblTiempoEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblTiempoEntidad.setName("TiempoEntidad");
        mapaLabels.put(lblTiempoEntidad.getName(), lblTiempoEntidad);
        panelParamsEntidad.add(lblTiempoEntidad);
        
        /* Muestra la aptitud que ha obtenido la entidad al acabar su ciclo de vida tras
         * realizarse el proceso de selección y haber sido evaluada */
        
        lblAptitudEntidad = new JLabel("Aptitud: 0");
        lblAptitudEntidad.setMinimumSize(new Dimension(50, 20));
        lblAptitudEntidad.setMaximumSize(new Dimension(110, 20));
        lblAptitudEntidad.setPreferredSize(new Dimension(50, 20));
        lblAptitudEntidad.setForeground(new Color(255, 255, 255));
        lblAptitudEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblAptitudEntidad.setName("AptitudEntidad");
        mapaLabels.put(lblAptitudEntidad.getName(), lblAptitudEntidad);
        panelParamsEntidad.add(lblAptitudEntidad);
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
	private JLabel obtenerLabelPorNombre(String nombre) {
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
	
	/**
	 * Centra el frame del panel de control sólo verticalmente respecto a la
	 * pantalla y mantiene la posición horizontal de la ventana.
	 */
	private void centrarFrameVerticalmente() {
		//Obtiene el alto de la pantalla a partir de sus dimensiones
        Dimension dimensionesPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int altoPantalla = dimensionesPantalla.height;
        /* Obtiene el alto del frame, que dependerá del tamaño que ocupen los componentes
         * dentro de la ventana al ajustarse automáticamente con pack(); */
        int altoFrame = this.getHeight();
        /* Calcula el punto donde la ventana estará alineada en el centro verticalmente,
         * a partir de obtener el punto medio del alto de la ventana menos el punto medio
         * del alto del frame, que nos daría la siguiente fórmula */
        int y = (altoPantalla - altoFrame) / 2;
        /* Establece la posición del frame en la coordenada x que ya tenía y la coordenada y
         * que hemos calculado y que coloca el frame centrado en vertical */
        this.setLocation(this.getX(), y);
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

	public JPanel getPanelDatosEntidad() {
		return panelDatosEntidad;
	}
}
