package vista;

import controlador.Controlador;

import controlador.Estado;
import modelo.Modelo;
import modelo.circuito.Meta;
import modelo.circuito.Obstaculo;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PJOGL;

/**
 * Muestra en pantalla un entorno gr�fico basado en Processing. En ella se dibujan todos los
 * elementos del modelo de datos con sus respectivas representaciones gr�ficas. Adem�s, si no
 * se le indica lo contrario, dicta el flujo del programa haciendo llamadas al controlador
 * desde su funci�n principal "draw()", por lo que cada frame desencadena un curso de acci�n
 * en el proceso evolutivo de las entidades, cuyo tiempo de vida se mide en fotogramas.
 * @author Alberto
 */
public class Ventana extends PApplet {
	
	/**
	 * Ancho fijo de la ventana
	 */
	private static final int ANCHO_VENTANA = 1280;
	/**
	 * Alto fijo de la ventana
	 */
	private static final int ALTO_VENTANA = 720;
	/**
	 * Instancia �nica de la ventana que nunca se va a reemplazar, manteniendo siempre el mismo
	 * objeto Ventana en toda la ejecuci�n
	 */
	private static Ventana instancia = null;
	/**
	 * Controlador principal del programa al que llama para ejecutar acciones y consultar datos
	 */
	private Controlador controlador;
	/**
	 * N�mero de frames que han pasado desde que ha empezado a actuar una nueva generaci�n hasta
	 * termina su ciclo de vida. Se reinicia cada vez que se reproducen las entidades
	 */
	private int numFramesGen;
	/**
	 * Representaci�n gr�fica de las entidades, que ser� una forma pre-procesada al inicio del
	 * programa para mejorar el rendimiento cuando se dibuje
	 */
	private PShape humanoide;
	/**
	 * Flag que indica si debe dibujar las flechas de direcci�n sobre todas las entidades
	 */
	private boolean modoDebug;
	
	/**
	 * Inicializa la instancia �nica de la ventana. No se puede llamar directamente desde fuera
	 * @param controlador que le llega del m�todo de "f�brica"
	 */
	private Ventana(Controlador controlador) {
		this.controlador = controlador;
	}
	
	/**
	 * 
	 * @param controlador con el que se comunica durante la ejecuci�n
	 * @return la instancia �nica de la Ventana, sea o no nueva
	 */
	public static Ventana crearVentana(Controlador controlador) {
		//Si no existe la instancia, crea una nueva a trav�s del constructor privado
		if (instancia == null) {
			instancia = new Ventana(controlador);
		}
		//Si ya existe la instancia, devuelve la misma
		return instancia;
	}
	
	/**
	 * Realiza la configuraci�n que sea necesaria antes de renderizar la ventana
	 */
	public void settings() {
		/* Establece las dimensiones de la ventana y el motor de renderizado que se encargar�
		 * dibujar los gr�ficos, que en este caso ser� P2D. Este motor internamente utiliza
		 * OpenGL, la API de programaci�n de gr�ficos multiplataforma que mejorar� 
		 * considerablemente el rendimiento del programa respecto al motor por defecto.
		 */
		size(ANCHO_VENTANA, ALTO_VENTANA, P2D);
		PJOGL.setIcon("gene_icon.png"); //Establece icono de ventana
		//Establece en nivel de suavizado de los bordes de las figuras dibujadas en pantalla
		smooth(16); 
	}
	
	/**
	 * Realiza la configuraci�n que sea necesaria despu�s de renderizar la ventana
	 */
	public void setup() {
		centrarVentana();
		//Impide que se pueda redimensionar arrastrando en los bordes del frame
        windowResizable(false); 
		windowTitle("Circuito Gen�tico");
		frameRate(60); //Establece el n�mero de frames por segundo a 60 
		stroke(0);
		background(255);
		/* Deja procesada en la cach� interna del motor de gr�ficos la forma de las entidades
		 * para que solo tenga que generarla una vez y cuando haga falta dibujarla llamar al 
		 * m�todo shape(humanoide) sin que tenga que crearla desde cero
		 */
		humanoide = crearFormaEntidad();
		/* Inicializa el contador de frames a 0 para que lo pueda incrementar la poblaci�n
		 * en cada generaci�n que sucede 
		 */
		numFramesGen = 0;
		modoDebug = false; //Inactivo por defecto
	}
	
	/**
	 * Funci�n principal de la ventana que se llamar� cada vez que se deba dibujar un frame
	 * en pantalla de acuerdo a los fotogramas por segundo que hemos establecido antes.
	 * Aqu� se actualizar� constantemente la pantalla y por tanto se dibujar�n todos los
	 * elementos que deban estar presentes para que que siempre permanezcan visibles.
	 * Adem�s, en caso de que se le indique desde el controlador que el proceso evolutivo
	 * no est� parado, llama a este para que haga avanzar el flujo del programa y no pase a
	 * dibujar el siguiente frame hasta que los datos del modelo se actualicen
	 */
	public void draw() {
		//Pinta siempre el fondo para que no queden restos de lo dibujado en el anterior frame
		background(255); 
		stroke(0);
		drawFramerate(); //Muestra a cu�ntos frames por segundo va
		drawEstado(controlador.getEstado()); //Muestra el estado en el que se encuentra el proceso
		//Dibuja todos los elementos del circuito que tengamos seleccionado (meta y obst�culo)
		drawCircuito(); 
		/* Si el controlador no se encuentra parado, ejecuta un ciclo en el proceso evolutivo
		 * para que este pueda avanzar y realizar toda su l�gica interna 
		 */
		if(!controlador.isParado()) {
			controlador.manipularPoblacion();
		} else if (controlador.getEstado() == Estado.PAUSADO) {
			/* Si el proceso se encuentra pausado, debe continuar mostrando las entidades que no
			 * hayan chocado aunque no est�n en movimiento, para que no desaparezcan en pantalla */
			controlador.getVisualizador().mostrarEntidadesActivas();
		}
	}

	/**
	 * Evento que provoca que cuando pinchemos con el rat�n en una de las entidades que se muestran
	 * en pantalla, se seleccione esa entidad y pueda pasar a monitorizarla en tiempo real. Se hace
	 * una llamada al controlador de ventos pas�ndole la posici�n del rat�n para determinar cual
	 * de las entidades es la que el usuario ha seleccionado.
	 */
	public void mousePressed() {
		controlador.getControladorEventos().seleccionarEntidad(new PVector(mouseX, mouseY));
	}

	/**
	 * Evento que provoca que seg�n la tecla que se haya pulsado, se llame en el controlador de 
	 * eventos el evento que le corresponde, de forma que realice la misma funci�n que si 
	 * pulsaras en los botones del panel de control. 
	 */
	public void keyPressed() {
		/* Para la barra espaciadora tendr� que determinar el controlador qu� evento debe
		 * realizar seg�n las condiciones que se den y el estado del proceso, pudiendo 
		 * empezar, continuar, pausar o reanudar el proceso seg�n la situaci�n.
		 */
		if(key == ' ') {
			controlador.getControladorEventos().realizarEventoEspacio();
			return;
		}
		if(key == 'a' || key == 'A') {
			//Activa o desactiva el modo autom�tico
			controlador.getControladorEventos().cambiarModo();
			return;
		}
		if(key == 'r' || key == 'R') {
			//Reinicia el proceso al estado inicial
			controlador.getControladorEventos().reiniciar();
			return;
		}
		if(key == 'd' || key == 'D') {
			/* Este es el �nico caso en el que no hace falta llamar a un evento del
			 * controlador, ya que lo que hace es activar o desactivar la flag del modo
			 * debug, para que cuando se muestren las entidades decida si tiene que dibujar
			 * sus flechas de direcci�n o no */
			modoDebug = !modoDebug;
			return;
		}
	}
	
	/**
	 * Dibuja la meta y los obst�culos del modelo, que est�n definidos por el circuito actual
	 */
	private void drawCircuito() {
		Modelo modelo = controlador.getModelo();
		drawMeta(modelo.getMeta());
		drawObstaculos(modelo.getObstaculos());
	}
	
	/**
	 * Dibuja la meta, que ser� una elipse, a partir de su posici�n y dimensiones
	 * @param meta
	 */
	private void drawMeta(Meta meta) {
		/* Se superpone una matriz de transformaci�n para realizarlas �nicamente sobre el
		 * elemento que vamos a dibujar y que las transformaciones no afecten a lo dem�s */
		pushMatrix();
		/* Trasladamos el eje de coordenadas hacia la posici�n de la meta, para que sea el
		 * punto de origen de coordenadas nuevo y podamos realizar el dibujado y otras 
		 * transformaciones desde ah� */
		translate(meta.getPosicion().x, meta.getPosicion().y);
		//Establecemos las propiedades con el que se dibujar� la figura
        fill(0, 255, 0); 
        strokeWeight(4); 
        /* Indicamos que la posici�n desde donde debe dibujar la elipse es su centro, de forma
         * que si posicionamos la elipse en (0, 0), el centro est� en el origen de coordenadas
         * y las dimensiones partir�n desde ese punto.
         */
        ellipseMode(CENTER);
        /* Dibujamos una elipse en el nuevo origen de coordenadas despu�s de trasladar los ejes
         * a la posici�n que deber�a tener la meta en la ventana. Las dimensiones, en este caso
         * los ejes de ela elipse, ser�n su ancho y alto
         */
        ellipse(0, 0, meta.getAncho(), meta.getAlto());
        /* Cuando se termine de transformar, sacamos la matriz de la pila para volver al 
         * contexto anterior, es decir, el sistema de coordenadas vuelva al origen de la
         * ventana y no se apliquen las transformaciones a lo que venga despu�s */
        popMatrix();
        strokeWeight(1);
	}
	
	/**
	 * Dibuja cada uno de los obst�culo que tendr� el circuito
	 * @param obstaculos: colecci�n de obst�culos
	 */
	private void drawObstaculos(Obstaculo[] obstaculos) {
		for(Obstaculo obs: obstaculos) {
			drawObstaculo(obs);
		}
	}
	
	/**
	 * Dibuja un obst�culo, que tendr� forma de rect�ngulo, con unas dimensiones y rotado
	 * en el �ngulo que le haya sido asignado
	 * @param obstaculo
	 */
	private void drawObstaculo(Obstaculo obstaculo) {
		pushMatrix();
        stroke(0);
        fill(165);
        //Movemos el sistema de coordenadas a la posici�n donde debe estar el obst�culo
        translate(obstaculo.getPosicion().x, obstaculo.getPosicion().y);
        /* Aplicamos una transformaci�n de rotaci�n sobre los ejes de coordenadas, para 
         * que todo lo que dibujemos en esta matriz aparezca rotado en el �ngulo indicado.
         * Esto es posible ya que si alteramos la disposici�n de los ejes, lo que se
         * encuentra dentro se representa de igual manera en relativo a estos, solo que 
         * el punto de vista desde fuera del sistema de coordenadas lo visualiza
         * en un �ngulo distinto
         */
        rotate(obstaculo.getAngulo());
        //Dibujamos el rect�ngulo con su ancho y alto en el origen de coordenadas
        rectMode(CENTER);
        rect(0, 0, obstaculo.getAncho(), obstaculo.getAlto());
        popMatrix();
	}
	
	/**
	 * Dibuja una entidad con la forma pre-procesada que hemos definido al inicio,
	 * en la posici�n en la que se encuentra y mirando en la direcci�n que indique
	 * su vector de velocidad. 
	 * @param posicion en la que se encuentra la entidad
	 * @param velocidad con la que se desplaza la entidad
	 * @param monitorizada: si la entidad debe resaltarse con un contorno distinto para
	 * destacarla sobre el resto.
	 */
	public void drawEntidad(PVector posicion, PVector velocidad, boolean monitorizada) {
		pushMatrix();
		//Desplazamos el origen de coordenadas a la posici�n de la entidad
		translate(posicion.x, posicion.y);
		/* Rotamos el sistema de coordenadas en el �ngulo determinado por su velocidad, 
		 * para que se visualice a la entidad orientada en la direcci�n en la que se est�
		 * moviendo actualmente. El �ngulo se obtiene a partir de obtener el arcotangente
		 * de la componente x e y del vector velocidad, que trigonom�tricamente equivale
		 * al �ngulo entre el lado adyacente y opuesto del tri�ngulo formado por las
		 * coordenadas del vector, dando como resultado la direcci�n y sentido de movimiento
		 */
		rotate(atan2(velocidad.y, velocidad.x));
		//Si est� monitorizada resalta el borde de la figura con un color, si no lo deja en negro
		int colorStroke = monitorizada ? color(204, 0, 255) : color(0, 0, 0);
		humanoide.setStroke(colorStroke); //le asigna el color del borde 
		/* Dibuja la figura humanoide que hemos definido en la configuraci�n inicial de la ventana
		 * que al estar pre-procesada no tendr� que programar de nuevo todas sus propiedades y 
		 * solo habr� que preocuparse de que represente la figura en la posici�n y rotaci�n 
		 * que le corresponde a la entidad en ese momento, ya que todas tienen la misma apariencia
		 */
		shape(humanoide);
		/* Si el modo debug est� activado, dibujar� adem�s una flecha de direcci�n que representa
		 * el movimiento que est� realizando a partir de su vector de velocidad. Esto puede 
		 * ralentizar severamente el programa */
		if(modoDebug == true) {
			drawFlechaDireccion(velocidad);
		}
		popMatrix();
	}
	
	/**
	 * Dibuja una flecha de direcci�n que representa lo r�pido que se est� desplazando
	 * una entidad y en qu� direcci�n y sentido lo est� haciendo, a partir de su velocidad
	 * @param velocidad que determina el desplazamiento de la entidad
	 */
	private void drawFlechaDireccion(PVector velocidad) {
		/* La rapidez con la que se mueve la entidad viene determinada por la magnitud del vector
		 * de la velocidad, de forma que cuanto m�s alto sea su valor, m�s posiciones se desplaza
		 * en una direcci�n y viceversa. Como la velocidad tiene valores demasiado peque�os para 
		 * ser visualizados, se multiplica su magnitud por un valor arbitrario para que la magnitud
		 * se represente de manera m�s clara, sin perder fidelidad a la hora de distinguir c�mo de
		 * r�pido se mueve una entidad en comparaci�n con otra. */
		float magnitudVelocidad = velocidad.mag() * 5;
		/* Se dibuja una forma de flecha a partir de l�neas de color rojo. La longitud de esa flecha,
		 * es decir, la posici�n del punto final de la l�nea respecto al origen de coordenadas de la 
		 * entidad, viene determinada por la magnitud, siendo directamente proporcional a su valor.
		 */
		stroke(255,0,0);
		line(0, 0, magnitudVelocidad, 0);
		// Las lineas que forman la punta de la flecha parten del mismo punto final que la anterior
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, -5);
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, 5);
		stroke(0);
	}
	
	/**
	 * Dibuja una ruta que ha realizado una entidad que ha completado el objetivo de llegar a 
	 * la meta en el tiempo indicado desde su punto inicial. Para ello obtiene sus genes en forma de
	 * colecci�n de vectores que equivalen a todas las fuerzas que se le han aplicado para poder
	 * llegar en el n�mero de frames indicado. Con esos genes podr� trazar las l�neas a partir de
	 * simular los movimientos que ha ido realizando en cada frame
	 * @param ruta: todos los vectores de fuerzas que se han aplicado
	 * @param tiempoObtenido: el tiempo en frames que ha necesitado la entidad para llegar
	 * a la meta
	 */
	public void drawRutaOptima(PVector[] ruta, int tiempoObtenido) {
		//Se pone el "pincel" a rojo para pintar la linea de la ruta
		stroke(255,0,0);
		strokeWeight(3);
		//Almacena una copia del vector de la posici�n inicial de la entidad
		PVector posicion = controlador.getModelo().getPoblacion().getPosInicial().copy();
		//Dibuja una elipse que representa el punto inicial de la ruta
		ellipseMode(CENTER);
		fill(255);
		ellipse(posicion.x, posicion.y, 20, 20);
		//Inicializa los vectores de velocidad y aceleraci�n para realizar el trazado
		PVector velocidad = new PVector(0,0);
		PVector aceleracion = new PVector(0,0);
		/* Por cada frame entre los que ha tardado en llegar a la meta (el tiempo obtenido),
		 * dibuja una l�nea entre una posici�n de origen y una final correspondiente al 
		 * movimiento que se ha realizado en ese frame. Para ello deber� simular el mismo
		 * desplazamiento */
		for(int i = 0; i < tiempoObtenido - 1; i++) {
			//Se le pasa como argumento todos los vectores de movimiento para que pueda trazar
			drawLineaRuta(ruta[i], posicion, velocidad, aceleracion);
		}
		strokeWeight(1);
	}

	/**
	 * Dibuja una l�nea desde un punto de origen a un punto final que simula el desplazamiento
	 * que ha realizado una entidad en un frame tras aplicarle la fuerza de un gen para 
	 * provocar su movimiento, que debemos simular para poder calcular el trazado 
	 * @param fuerza aplicada por el gen
	 * @param posicion antes de realizar el movimiento
	 * @param velocidad a la que se estaba moviendo antes de aplicarle la fuerza
	 * @param aceleracion a la que se le aplicar� la fuerza
	 */
	private void drawLineaRuta(PVector fuerza, PVector posicion, PVector velocidad, PVector aceleracion) {
		/* Almacena una copia de la posici�n antes de simular el siguiente movimiento para
		 * poder trazar la l�nea entre la posici�n de origen y la posici�n final */
		PVector posicionPrevia = posicion.copy();
		//Realiza el mismo movimiento que la entidad en un frame modificando sus vectores 
		simularMovimiento(fuerza, posicion, velocidad, aceleracion);
		//Traza la l�nea entre la posici�n antes del movimiento y despu�s del movimiento
		line(posicionPrevia.x, posicionPrevia.y, posicion.x, posicion.y);
		pushMatrix();
		//Solo deber� mostrar la flecha de direcci�n de velocidad si est� activado el modo debug
		if(modoDebug) {
			drawFlechaDireccion(velocidad, posicionPrevia);
		}
		popMatrix();
		stroke(255,0,0);
		//Reinicia la aceleraci�n a cero para preparar el siguiente movimiento
		aceleracion.mult(0);
	}
	
	/**
	 * Realiza el mismo movimiento que una entidad en un determinado frame a partir de la
	 * fuerza contenida en el gen correspondiente en ese momento. 
	 * @param fuerza aplicada por el gen
	 * @param posicion antes de realizar el movimiento
	 * @param velocidad a la que se estaba moviendo antes de aplicarle la fuerza
	 * @param aceleracion a la que se le aplicar� la fuerza
	 */
	private void simularMovimiento(PVector fuerza, PVector posicion, PVector velocidad, PVector aceleracion) {
		aceleracion.add(fuerza);
		velocidad.add(aceleracion);
		posicion.add(velocidad);
	}
	
	/**
	 * Al igual que se har�a cuando se activa el modo debug con las entidades activas, dibuja
	 * una flecha representando la direcci�n y rapidez con la que se ha desplazado desde la
	 * posici�n anterior a la actual, partiendo desde la posici�n de origen
	 * @param velocidad con la que se estaba moviendo en un frame en particular
	 * @param posicionPrevia en la que estaba antes de desplazarse con la velocidad
	 */
	private void drawFlechaDireccion(PVector velocidad, PVector posicionPrevia) {
		//Trasladamos el sistema de coordenadas a la posici�n anterior
		translate(posicionPrevia.x, posicionPrevia.y);
		//Rotamos en la direcci�n de la velocidad en este instante
		rotate(atan2(velocidad.y, velocidad.x));
		stroke(204, 0, 255);
		//Obtenemos su magnitud amplificada
		float magnitudVelocidad = velocidad.mag() * 5;
		//Dibujamos solo la punta de la flecha para que no tape la l�nea trazada de la ruta
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, -5);
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, 5);
	}
	
	/**
	 * Crea un objeto PShape que representa una forma humanoide para las entidades. El proceso de
	 * crear la forma consistir� en crear componentes individuales con sus caracter�sticas propias
	 * y a�adirlos a la forma que hace de contenedor como si fuera una agrupaci�n de formas, para
	 * conseguir el aspecto que deseamos que se muestre
	 * @return el objeto que almacena la forma que tendr�n todas las entidades
	 */
	private PShape crearFormaEntidad() {
		//Iniciamos una plantilla base para crear una forma y le indicamos que ser� una agrupaci�n de formas
		PShape humanoide = createShape(GROUP);
		/* Creamos el "cuerpo" de la forma, que ser� un tri�ngulo is�sceles cuyo v�rtice m�s "alto" se
		 * encontrar� en el origen de coordenadas de la entidad y el resto de v�rtices "bajos" se dibujar�n 
		 * en el eje negativo horizontal. De esta forma, ese v�rtice alto coincide con la posici�n de 
		 * la entidad en el eje de coordenadas de la ventana, y por tanto, cada vez que rotemos la figura
		 * de la entidad, el tri�ngulo apuntar� en la direcci�n en la que se mueve esta en todo momento. */
		PShape cuerpo = createShape(TRIANGLE, -25, -10, 0, 0, -25, 10);
		//Podemos personalizar las propiedades individuales de este componente
		cuerpo.setFill(color(0, 0, 255)); 
	    cuerpo.setStroke(color(0));
	    cuerpo.setStrokeWeight(2);
		humanoide.addChild(cuerpo); //A�adimos la forma a la forma contenedor para que forme parte de ella
		/* Crea la "cabeza" de la forma, que ser� una elipse cuyo centro estar� en el origen de coordenadas
		 * de la entidad y por tanto coincide con la posici�n de la entidad en ese instante. */
	    PShape cabeza = createShape(ELLIPSE, 0, 0, 10, 10);
	    cabeza.setFill(color(0, 0, 255)); 
	    cabeza.setStroke(color(0));
	    cabeza.setStrokeWeight(2);
	    humanoide.addChild(cabeza);
	    /* Crea las cuatro "extremidades" de la forma, cuyas formas son un conjunto de lineas delimitadas
	     * por unos v�rtices conectados entre s�. Las posiciones de esos v�rtices tienen una colocaci�n
	     * deliberada sobre el eje de coordenadas de la entidad, para que parezca que est�n conectadas
	     * al "cuerpo" de la figura y tenga el dise�o de una especie de humanoide con piernas y con los
	     * brazos levantados */
	    PShape extremidades = createShape();
	    extremidades.beginShape(LINES);
	    extremidades.stroke(0,0,255);
	    extremidades.vertex(-10, -5);
	    extremidades.vertex(5, -10);
	    extremidades.vertex(-10, 5);
	    extremidades.vertex(5, 10);
	    extremidades.vertex(-25, -5);
	    extremidades.vertex(-40, -5);
	    extremidades.vertex(-25, 5);
	    extremidades.vertex(-40, 5);
	    extremidades.endShape();
	    humanoide.addChild(extremidades);
		/* Una vez tenemos todos los componentes creados y a�adidos a la base, podemos devolver la forma
		 * entera funcionando como una sola figura que podremos llamar en cualquier momento y cuyas 
		 * propiedades ya estar�n creadas y solo deber� cargarlas desde la cach� del motor gr�fico ahorrando
		 * tiempo de ejecuci�n. Como las figuras funcionan como una unidad, si cambiamos las propiedades de
		 * esta o realizamos alguna transformaci�n, se aplica al conjunto.
		 */
	    return humanoide;
	}
	
	/**
	 * Dibuja un texto que actualiza los fotogramas por segundo a los que se actualiza la ventana
	 * a partir de la variable que nos devuelve su valor en cada frame
	 */
	private void drawFramerate() {
		fill(80);
		//Damos propiedades al texto
		textSize(16);
		textAlign(LEFT, CENTER);
		//Le asignamos la cadena que debe mostrar y en que posici�n
		text("Framerate: " + round(frameRate), 10, 15); //Redondeamos el valor ya que es decimal
	}
	
	/**
	 * Dibuja un texto que muestra el estado actual del proceso evolutivo para mantener al usuario
	 * informado de en qu� situaci�n se encuentra tras interactuar con la interfaz. Su valor
	 * se actualiza desde el propio proceso cuando detecte que se ha cambiado de estado.
	 * @param estado en el que se encuentra
	 */
	private void drawEstado(Estado estado) {
		textSize(18);
		textAlign(LEFT, CENTER);
		fill(80); 
		text("Estado: ", 9, 45);
		/* El valor del estado se mostrar� en el color que le ha sido asignado como propiedad a
		 * la constante del Enum correspondiente. De esta forma, cada uno de los estados tiene
		 * un color RGB que se podr� obtener para representarlos de una manera m�s diferenciada */
		int[] colorEstado = estado.getColor();
		//Coloreamos el texto del valor a partir de los valores RGB almacenados en el estado
		fill(colorEstado[0], colorEstado[1], colorEstado[2]); 
		//Mostramos el estado resaltado en el color adecuado
		text(estado.getTexto(), 9 + textWidth("Estado: "), 45);
	}
	
	/**
	 * Evento que se dispara cuando el usuario trata de redimensionar la ventana. A pesar
	 * de haber desactivado la posibilidad de hacerlo en la configuraci�n de la ventana, el motor
	 * "P2D" de OpenGL que estamos utilizando tiene un problema en el que se sigue permitiendo
	 * maximizar la ventana desde la barra de t�tulo. Por ese motivo, si se trata de hacerlo,
	 * debe volverse a redimensionar inmediatamente al tama�o original y fijado establecido 
	 * para la ventana, y volver a centarla ya que puede tambien descolocarse en la pantalla
	 */
	public void windowResized() {
		//Redimensiona la ventana utilizando las constantes definidas para esta propiedad
        windowResize(ANCHO_VENTANA, ALTO_VENTANA); 
        centrarVentana();
    }
	
	/**
	 * A pesar de que la ventana de Processing se muestra por defecto en el centro de la pantalla
	 * tanto vertical como horizontalmente, nos interesa darle un ligero desplazamiento a la derecha
	 * para dar espacio a la otra interfaz, el panel de control, de mostrarse sin que se solapen 
	 * cuando se inicia el programa. Por ese motivo tenemos que centrarla "manualmente" a partir
	 * de las dimensiones de la pantalla y de nuestra ventana.
	 */
	private void centrarVentana() {
		/* Si le restamos el punto medio del ancho y alto de la pantalla al punto medio del ancho y 
		 * alto de la ventana, nos queda la posici�n en la que tiene que encontrarse la ventana
		 * para mostrarse centrada vertical y/o horizontalmente sobre esta. Despu�s de eso le
		 * a�adimos 25 pixeles a la posici�n horizontal para que est� ligeramente desplazado del centro
		 */
		int posicionVentanaX = (displayWidth - width) / 2 + 25; 
		/* Se puede convertir pantalla/2 (punto medio pantalla) - ventana/2 (punto medio pantalla) en
		 * (pantalla - ventana) / 2 para obtener el punto centrado 
		 */
        int posicionVentanaY = (displayHeight - height) / 2;
        //Establecemos la posici�n de la superficie de la ventana
        surface.setLocation(posicionVentanaX, posicionVentanaY);
	}
	
	public int getNumFramesGen() {
		return numFramesGen;
	}

	public void setNumFramesGen(int numFramesGen) {
		this.numFramesGen = numFramesGen;
	}
	
}
