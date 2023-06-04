package vista.panel_control;

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

public class PanelControl {
	
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
	
	private HashMap<String, JLabel> mapaLabels;
	private JComboBox<String> cBoxCircuito;
	private JButton btnSalir;

	private Controlador controlador;


	public PanelControl(Controlador controlador) {
		
		this.controlador = controlador;
		
		// Crear JFrame principal
        JFrame frmCircuitoGenetico = new JFrame("Circuito Gen\u00E9tico - Panel de Control\r\n");
        frmCircuitoGenetico.setLocation(new Point(0, 0));
        frmCircuitoGenetico.setName("frameCircuitoGenetico");
        frmCircuitoGenetico.setTitle("Panel de Control");
        frmCircuitoGenetico.setIconImage(Toolkit.getDefaultToolkit().getImage(PanelControl.class.getResource("/img/gene_icon.png")));
        frmCircuitoGenetico.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmCircuitoGenetico.setPreferredSize(new Dimension(335, 732));
        frmCircuitoGenetico.setResizable(false);
        mapaLabels = new HashMap<String, JLabel>();

        // Crear JPanel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(new Color(46, 10, 84));
        panelPrincipal.setLayout(new BorderLayout(0, 0));
        frmCircuitoGenetico.setContentPane(panelPrincipal);

        JPanel panelTop = new JPanel();
        panelTop.setOpaque(false);
        panelPrincipal.add(panelTop, BorderLayout.NORTH);
        
        JLabel lblTitulo = new JLabel("Circuito Gen\u00E9tico");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(255, 255, 255));
        lblTitulo.setFont(new Font("Lato", Font.BOLD, 18));
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
        
        JPanel panelContenido = new JPanel();
        panelContenido.setOpaque(false);
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        
        // Primera sección
        JPanel panelControles = new JPanel();
        panelControles.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Controles", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
        panelControles.setBackground(new Color(99, 9, 177));
        panelControles.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelContenido.add(panelControles);

        lblGeneracion = new JLabel("Generación: 0");
        lblGeneracion.setForeground(new Color(255, 255, 255));
        lblGeneracion.setFont(new Font("Lato", Font.BOLD, 14));
        lblGeneracion.setName("Generacion");
        mapaLabels.put(lblGeneracion.getName(), lblGeneracion);
        panelControles.add(lblGeneracion);

        cbModoAutomatico = new JCheckBox("Modo Automático");
        cbModoAutomatico.setVerticalTextPosition(SwingConstants.BOTTOM);
        cbModoAutomatico.setHorizontalAlignment(SwingConstants.TRAILING);
        cbModoAutomatico.setHorizontalTextPosition(SwingConstants.LEADING);
        cbModoAutomatico.setForeground(new Color(255, 255, 255));
        cbModoAutomatico.setFont(new Font("Lato", Font.BOLD, 14));
        cbModoAutomatico.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cbModoAutomatico.setToolTipText("");
        cbModoAutomatico.setFocusable(false);
        panelControles.add(cbModoAutomatico);
        
        btnProceder = new JButton("Empezar");
        btnProceder.setFont(new Font("Lato", Font.BOLD, 16));
        btnProceder.setBackground(new Color(141, 71, 201));
        btnProceder.setForeground(new Color(255, 255, 255));
        btnProceder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnProceder.setToolTipText("Comienza una nueva generaci\u00F3n ");
        btnProceder.setFocusable(false);
        panelControles.add(btnProceder);

        btnPausar = new JButton("Pausar");
        btnPausar.setFont(new Font("Lato", Font.BOLD, 16));
        btnPausar.setBackground(new Color(141, 71, 201));
        btnPausar.setForeground(new Color(255, 255, 255));
        btnPausar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPausar.setToolTipText("Pausa/Reanuda el movimiento de las entidades");
        btnPausar.setFocusable(false);
        btnPausar.setEnabled(false);
        panelControles.add(btnPausar);
        
        btnReiniciar = new JButton("Reiniciar");
        btnReiniciar.setFont(new Font("Lato", Font.BOLD, 16));
        btnReiniciar.setBackground(new Color(141, 71, 201));
        btnReiniciar.setForeground(new Color(255, 255, 255));
        btnReiniciar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReiniciar.setToolTipText("Termina de ejecutar el proceso evolutivo y vuelve al estado inicial");
        btnReiniciar.setFocusable(false);
        btnReiniciar.setEnabled(false);
        panelControles.add(btnReiniciar);
        
        btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Lato", Font.BOLD, 16));
        btnSalir.setBackground(new Color(141, 71, 201));
        btnSalir.setForeground(new Color(255, 255, 255));
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.setToolTipText("Cierra el programa");
        btnSalir.setFocusable(false);
        panelControles.add(btnSalir);
 
        Component separador1 = Box.createVerticalStrut(10);
        panelContenido.add(separador1);
        
        // Segunda sección
        JPanel panelConfig = new JPanel();
        panelConfig.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Configuraci\u00F3n", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
        panelConfig.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelConfig.setBackground(new Color(99, 9, 177));
        panelConfig.setLayout(new BoxLayout(panelConfig, BoxLayout.Y_AXIS));
        panelContenido.add(panelConfig);
        
        JPanel panelCircuito = new JPanel();
        panelCircuito.setOpaque(false);
        panelCircuito.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCircuito.setLayout(new FlowLayout());
        panelConfig.add(panelCircuito);
        
        JLabel lblCircuito = new JLabel("Circuito");
        lblCircuito.setForeground(new Color(255, 255, 255));
        lblCircuito.setFont(new Font("Lato", Font.BOLD, 12));
        lblCircuito.setLabelFor(cBoxCircuito);
        panelCircuito.add(lblCircuito);
        
        cBoxCircuito = rellenarCboxCircuito();
        cBoxCircuito.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cBoxCircuito.setFont(new Font("Lato", Font.BOLD, 12));
        cBoxCircuito.setToolTipText("Selecciona un circuito para las entidades ");
        cBoxCircuito.setSelectedIndex(0);
        cBoxCircuito.setMaximumRowCount(10);
        cBoxCircuito.setEditable(true);
        panelCircuito.add(cBoxCircuito);

        JPanel panelPoblacionTotal = new JPanel();
        panelPoblacionTotal.setOpaque(false);
        panelPoblacionTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPoblacionTotal.setLayout(new FlowLayout());
        panelConfig.add(panelPoblacionTotal);
        
        JLabel lblPoblacionTotal = new JLabel("Población total");
        lblPoblacionTotal.setForeground(new Color(255, 255, 255));
        lblPoblacionTotal.setFont(new Font("Lato", Font.BOLD, 12));
        panelPoblacionTotal.add(lblPoblacionTotal);
        
        spPoblacion = new JSpinner(new SpinnerNumberModel(1000, 4, 15000, 50));
        lblPoblacionTotal.setLabelFor(spPoblacion);
        spPoblacion.setFont(new Font("Lato", Font.BOLD, 12));
        spPoblacion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spPoblacion.setToolTipText("Modifica el n\u00FAmero de entidades que se generan");
        spPoblacion.setName("NumEntidades");
        panelPoblacionTotal.add(spPoblacion);

        JPanel panelTasaMutacion = new JPanel();
        panelTasaMutacion.setOpaque(false);
        panelTasaMutacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTasaMutacion.setLayout(new FlowLayout());
        panelConfig.add(panelTasaMutacion);
        
        JLabel lblTasaMutacion = new JLabel("Tasa de mutación (%)");
        lblTasaMutacion.setForeground(new Color(255, 255, 255));
        lblTasaMutacion.setFont(new Font("Lato", Font.BOLD, 12));
        panelTasaMutacion.add(lblTasaMutacion);
        
        spMutacion = new JSpinner(new SpinnerNumberModel(20, 0, 100, 1));
        lblTasaMutacion.setLabelFor(spMutacion);
        spMutacion.setFont(new Font("Lato", Font.BOLD, 12));
        spMutacion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spMutacion.setToolTipText("Modifica la probabilidad de que muten los genes");
        spMutacion.setName("TasaMutacion");
        panelTasaMutacion.add(spMutacion);

        JPanel panelTiempoObjetivo = new JPanel();
        panelTiempoObjetivo.setOpaque(false);
        panelTiempoObjetivo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTiempoObjetivo.setLayout(new FlowLayout());
        panelConfig.add(panelTiempoObjetivo);
        
        JLabel lblTiempoObjetivo = new JLabel("Tiempo objetivo (frames)");
        lblTiempoObjetivo.setForeground(new Color(255, 255, 255));
        lblTiempoObjetivo.setFont(new Font("Lato", Font.BOLD, 12));
        panelTiempoObjetivo.add(lblTiempoObjetivo);
        
        spTiempoObjetivo = new JSpinner(new SpinnerNumberModel(140, 2, 2000, 1));
        lblTiempoObjetivo.setLabelFor(spTiempoObjetivo);
        spTiempoObjetivo.setFont(new Font("Lato", Font.BOLD, 12));
        spTiempoObjetivo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spTiempoObjetivo.setToolTipText("Modifica el tiempo en frames que deben lograr las entidades para llegar a la meta");
        spTiempoObjetivo.setName("TiempoObjetivo");
        panelTiempoObjetivo.add(spTiempoObjetivo);

        JPanel panelTiempoVida = new JPanel();
        panelTiempoVida.setOpaque(false);
        panelTiempoVida.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTiempoVida.setLayout(new FlowLayout());
        panelConfig.add(panelTiempoVida);
        
        JLabel lblTiempoVida = new JLabel("Tiempo de vida (frames)");
        lblTiempoVida.setForeground(new Color(255, 255, 255));
        lblTiempoVida.setFont(new Font("Lato", Font.BOLD, 12));
        panelTiempoVida.add(lblTiempoVida);
        
        spTiempoVida = new JSpinner(new SpinnerNumberModel(400, 10, 2001, 5));
        lblTiempoVida.setLabelFor(spTiempoVida);
        spTiempoVida.setFont(new Font("Lato", Font.BOLD, 12));
        spTiempoVida.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spTiempoVida.setToolTipText("Modifica el tiempo en frames que viven las entidades");
        spTiempoVida.setName("TiempoVida");
        panelTiempoVida.add(spTiempoVida);

        Component separador2 = Box.createVerticalStrut(10);
        panelContenido.add(separador2);
        
        // Tercera sección
        
        JPanel panelDatos = new JPanel();
        panelDatos.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Monitorizaci\u00F3n", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
        panelDatos.setBackground(new Color(99, 9, 177));
        panelDatos.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));
        panelContenido.add(panelDatos);
        
        JPanel panelDatosGeneracion = new JPanel();
        panelDatosGeneracion.setOpaque(false);
        panelDatosGeneracion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelDatosGeneracion.setLayout(new BoxLayout(panelDatosGeneracion, BoxLayout.X_AXIS));
        panelDatos.add(panelDatosGeneracion);
        
        JPanel panelTotalGeneraciones = new JPanel();
        panelTotalGeneraciones.setOpaque(false);
        panelTotalGeneraciones.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelTotalGeneraciones.setLayout(new BorderLayout());
        panelDatosGeneracion.add(panelTotalGeneraciones);
        
        JLabel lblDatosTotalGeneraciones = new JLabel("Datos Totales");
        lblDatosTotalGeneraciones.setForeground(new Color(255, 255, 255));
        lblDatosTotalGeneraciones.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDatosTotalGeneraciones.setFont(new Font("Lato", Font.BOLD, 12));
        panelTotalGeneraciones.add(lblDatosTotalGeneraciones, BorderLayout.NORTH);
        
        JPanel panelTotalGeneracionesContenido = new JPanel();
        panelTotalGeneracionesContenido.setOpaque(false);
        panelTotalGeneracionesContenido.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelTotalGeneracionesContenido.setLayout(new BoxLayout(panelTotalGeneracionesContenido,BoxLayout.Y_AXIS));
        panelTotalGeneraciones.add(panelTotalGeneracionesContenido, BorderLayout.CENTER);
        
        lblTiempoRecord = new JLabel("Tiempo record (frames): 0");
        lblTiempoRecord.setForeground(new Color(255, 255, 255));
        lblTiempoRecord.setFont(new Font("Lato", Font.BOLD, 12));
        lblTiempoRecord.setName("TiempoRecord");
        mapaLabels.put(lblTiempoRecord.getName(), lblTiempoRecord);
        panelTotalGeneracionesContenido.add(lblTiempoRecord);
        
        lblMejorAptitud = new JLabel("Mejor aptitud: 0");
        lblMejorAptitud.setForeground(new Color(255, 255, 255));
        lblMejorAptitud.setFont(new Font("Lato", Font.BOLD, 12));
        lblMejorAptitud.setName("MejorAptitud");
        mapaLabels.put(lblMejorAptitud.getName(), lblMejorAptitud);
        panelTotalGeneracionesContenido.add(lblMejorAptitud);
        
        lblMetas = new JLabel("Metas alcanzadas: 0");
        lblMetas.setForeground(new Color(255, 255, 255));
        lblMetas.setFont(new Font("Lato", Font.BOLD, 12));
        lblMetas.setName("Metas");
        mapaLabels.put(lblMetas.getName(), lblMetas);
        panelTotalGeneracionesContenido.add(lblMetas);
        
        lblColisiones = new JLabel("Colisiones: 0");
        lblColisiones.setForeground(new Color(255, 255, 255));
        lblColisiones.setFont(new Font("Lato", Font.BOLD, 12));
        lblColisiones.setName("Colisiones");
        mapaLabels.put(lblColisiones.getName(), lblColisiones);
        panelTotalGeneracionesContenido.add(lblColisiones);

        JPanel panelGeneracionActual = new JPanel();
        panelGeneracionActual.setOpaque(false);
        panelGeneracionActual.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelGeneracionActual.setLayout(new BorderLayout());
        panelDatosGeneracion.add(panelGeneracionActual);
        
        JLabel lblDatosGeneracionActual = new JLabel("Datos Última Generación");
        lblDatosGeneracionActual.setForeground(new Color(255, 255, 255));
        lblDatosGeneracionActual.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDatosGeneracionActual.setFont(new Font("Lato", Font.BOLD, 12));
        panelGeneracionActual.add(lblDatosGeneracionActual, BorderLayout.NORTH);
        
        JPanel panelGeneracionActualContenido = new JPanel();
        panelGeneracionActualContenido.setOpaque(false);
        panelGeneracionActualContenido.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelGeneracionActualContenido.setLayout(new BoxLayout(panelGeneracionActualContenido,BoxLayout.Y_AXIS));
        panelGeneracionActual.add(panelGeneracionActualContenido, BorderLayout.CENTER);
        
        lblTiempoRecordActual = new JLabel("Tiempo record (frames): 0");
        lblTiempoRecordActual.setForeground(new Color(255, 255, 255));
        lblTiempoRecordActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblTiempoRecordActual.setName("TiempoRecordActual");
        mapaLabels.put(lblTiempoRecordActual.getName(), lblTiempoRecordActual);
        panelGeneracionActualContenido.add(lblTiempoRecordActual);
        
        lblMejorAptitudActual = new JLabel("Mejor aptitud: 0");
        lblMejorAptitudActual.setForeground(new Color(255, 255, 255));
        lblMejorAptitudActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblMejorAptitudActual.setName("MejorAptitudActual");
        mapaLabels.put(lblMejorAptitudActual.getName(), lblMejorAptitudActual);
        panelGeneracionActualContenido.add(lblMejorAptitudActual);
        
        lblMetasActual = new JLabel("Metas alcanzadas: 0");
        lblMetasActual.setForeground(new Color(255, 255, 255));
        lblMetasActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblMetasActual.setName("MetasActual");
        mapaLabels.put(lblMetasActual.getName(), lblMetasActual);
        panelGeneracionActualContenido.add(lblMetasActual);
        
        lblColisionesActual = new JLabel("Colisiones: 0");
        lblColisionesActual.setForeground(new Color(255, 255, 255));
        lblColisionesActual.setFont(new Font("Lato", Font.BOLD, 12));
        lblColisionesActual.setName("ColisionesActual");
        mapaLabels.put(lblColisionesActual.getName(), lblColisionesActual);
        panelGeneracionActualContenido.add(lblColisionesActual);
        
        JPanel panelDatosEntidad = new JPanel();
        panelDatosEntidad.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panelDatosEntidad.setOpaque(true);
        panelDatosEntidad.setBackground(new Color(120, 33, 195));
        panelDatosEntidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelDatosEntidad.setLayout(new BorderLayout());
        panelDatos.add(panelDatosEntidad);
        
        lblEntidad = new JLabel("Entidad: -", SwingConstants.CENTER);
        lblEntidad.setForeground(new Color(255, 255, 255));
        lblEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblEntidad.setName("Entidad");
        lblEntidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        mapaLabels.put(lblEntidad.getName(), lblEntidad);
        panelDatosEntidad.add(lblEntidad, BorderLayout.NORTH);
        
        JPanel panelDatosEntidadContenido = new JPanel();
        panelDatosEntidadContenido.setOpaque(false);
        panelDatosEntidadContenido.setAlignmentY(Component.CENTER_ALIGNMENT);
        panelDatosEntidadContenido.setLayout(new BoxLayout(panelDatosEntidadContenido, BoxLayout.Y_AXIS));
        panelDatosEntidad.add(panelDatosEntidadContenido, BorderLayout.CENTER);
            
        lblPosicion = new JLabel("Posicion (px): (0,0)");
        lblPosicion.setForeground(new Color(255, 255, 255));
        lblPosicion.setFont(new Font("Lato", Font.BOLD, 12));
        lblPosicion.setName("Posicion");
        mapaLabels.put(lblPosicion.getName(), lblPosicion);
        panelDatosEntidadContenido.add(lblPosicion);
        
        lblVelocidad = new JLabel("Velocidad (px): (0,0)");
        lblVelocidad.setForeground(new Color(255, 255, 255));
        lblVelocidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblVelocidad.setName("Velocidad");
        mapaLabels.put(lblVelocidad.getName(), lblVelocidad);
        panelDatosEntidadContenido.add(lblVelocidad);
        
        lblAceleracion = new JLabel("Aceleración (px): (0,0)");
        lblAceleracion.setForeground(new Color(255, 255, 255));
        lblAceleracion.setFont(new Font("Lato", Font.BOLD, 12));
        lblAceleracion.setName("Aceleracion");
        mapaLabels.put(lblAceleracion.getName(), lblAceleracion);
        panelDatosEntidadContenido.add(lblAceleracion);
        
        lblDistancia = new JLabel("Distancia: 0");
        lblDistancia.setForeground(new Color(255, 255, 255));
        lblDistancia.setFont(new Font("Lato", Font.BOLD, 12));
        lblDistancia.setName("DistanciaEntidad");
        mapaLabels.put(lblDistancia.getName(), lblDistancia);
        panelDatosEntidadContenido.add(lblDistancia);
        
        lblDistMinEntidad = new JLabel("Distancia mínima: 0");
        lblDistMinEntidad.setForeground(new Color(255, 255, 255));
        lblDistMinEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblDistMinEntidad.setName("DistanciaMinEntidad");
        mapaLabels.put(lblDistMinEntidad.getName(), lblDistMinEntidad);
        panelDatosEntidadContenido.add(lblDistMinEntidad);
        
        lblEstado = new JLabel("Estado: -");
        lblEstado.setForeground(new Color(255, 255, 255));
        lblEstado.setFont(new Font("Lato", Font.BOLD, 12));
        lblEstado.setName("EstadoEntidad");
        mapaLabels.put(lblEstado.getName(), lblEstado);
        panelDatosEntidadContenido.add(lblEstado);
        
        lblTiempoEntidad = new JLabel("Tiempo obtenido: 0");
        lblTiempoEntidad.setForeground(new Color(255, 255, 255));
        lblTiempoEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblTiempoEntidad.setName("TiempoEntidad");
        mapaLabels.put(lblTiempoEntidad.getName(), lblTiempoEntidad);
        panelDatosEntidadContenido.add(lblTiempoEntidad);
        
        lblAptitudEntidad = new JLabel("Aptitud: 0");
        lblAptitudEntidad.setForeground(new Color(255, 255, 255));
        lblAptitudEntidad.setFont(new Font("Lato", Font.BOLD, 12));
        lblAptitudEntidad.setName("AptitudEntidad");
        mapaLabels.put(lblAptitudEntidad.getName(), lblAptitudEntidad);
        panelDatosEntidadContenido.add(lblAptitudEntidad);

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
