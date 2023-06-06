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
 * Muestra en pantalla un entorno gráfico basado en Processing. En ella se dibujan todos los
 * elementos del modelo de datos con sus respectivas representaciones gráficas. Además, si no
 * se le indica lo contrario, dicta el flujo del programa haciendo llamadas al controlador
 * desde su función principal "draw()", por lo que cada frame desencadena un curso de acción
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
	 * Instancia única de la ventana que nunca se va a reemplazar, manteniendo siempre el mismo
	 * objeto Ventana en toda la ejecución
	 */
	private static Ventana instancia = null;
	/**
	 * Controlador principal del programa al que llama para ejecutar acciones y consultar datos
	 */
	private Controlador controlador;
	/**
	 * Número de frames que han pasado desde que ha empezado a actuar una nueva generación hasta
	 * termina su ciclo de vida. Se reinicia cada vez que se reproducen las entidades
	 */
	private int numFramesGen;
	/**
	 * Representación gráfica de las entidades, que será una forma pre-procesada al inicio del
	 * programa para mejorar el rendimiento cuando se dibuje
	 */
	private PShape humanoide;
	/**
	 * Flag que indica si debe dibujar las flechas de dirección sobre todas las entidades
	 */
	private boolean modoDebug;
	
	/**
	 * Inicializa la instancia única de la ventana. No se puede llamar directamente desde fuera
	 * @param controlador que le llega del método de "fábrica"
	 */
	private Ventana(Controlador controlador) {
		this.controlador = controlador;
	}
	
	/**
	 * 
	 * @param controlador con el que se comunica durante la ejecución
	 * @return la instancia única de la Ventana, sea o no nueva
	 */
	public static Ventana crearVentana(Controlador controlador) {
		//Si no existe la instancia, crea una nueva a través del constructor privado
		if (instancia == null) {
			instancia = new Ventana(controlador);
		}
		//Si ya existe la instancia, devuelve la misma
		return instancia;
	}
	
	/**
	 * Realiza la configuración que sea necesaria antes de renderizar la ventana
	 */
	public void settings() {
		/* Establece las dimensiones de la ventana y el motor de renderizado que se encargará
		 * dibujar los gráficos, que en este caso será P2D. Este motor internamente utiliza
		 * OpenGL, la API de programación de gráficos multiplataforma que mejorará 
		 * considerablemente el rendimiento del programa respecto al motor por defecto.
		 */
		size(ANCHO_VENTANA, ALTO_VENTANA, P2D);
		PJOGL.setIcon("gene_icon.png"); //Establece icono de ventana
		//Establece en nivel de suavizado de los bordes de las figuras dibujadas en pantalla
		smooth(16); 
	}
	
	/**
	 * Realiza la configuración que sea necesaria después de renderizar la ventana
	 */
	public void setup() {
		centrarVentana();
		//Impide que se pueda redimensionar arrastrando en los bordes del frame
        windowResizable(false); 
		windowTitle("Circuito Genético");
		frameRate(60); //Establece el número de frames por segundo a 60 
		stroke(0);
		background(255);
		/* Deja procesada en la caché interna del motor de gráficos la forma de las entidades
		 * para que solo tenga que generarla una vez y cuando haga falta dibujarla llamar al 
		 * método shape(humanoide) sin que tenga que crearla desde cero
		 */
		humanoide = crearFormaEntidad();
		/* Inicializa el contador de frames a 0 para que lo pueda incrementar la población
		 * en cada generación que sucede 
		 */
		numFramesGen = 0;
		modoDebug = false; //Inactivo por defecto
	}
	
	/**
	 * Función principal de la ventana que se llamará cada vez que se deba dibujar un frame
	 * en pantalla de acuerdo a los fotogramas por segundo que hemos establecido antes.
	 * Aquí se actualizará constantemente la pantalla y por tanto se dibujarán todos los
	 * elementos que deban estar presentes para que que siempre permanezcan visibles.
	 * Además, en caso de que se le indique desde el controlador que el proceso evolutivo
	 * no está parado, llama a este para que haga avanzar el flujo del programa y no pase a
	 * dibujar el siguiente frame hasta que los datos del modelo se actualicen
	 */
	public void draw() {
		//Pinta siempre el fondo para que no queden restos de lo dibujado en el anterior frame
		background(255); 
		stroke(0);
		drawFramerate(); //Muestra a cuántos frames por segundo va
		drawEstado(controlador.getEstado()); //Muestra el estado en el que se encuentra el proceso
		//Dibuja todos los elementos del circuito que tengamos seleccionado (meta y obstáculo)
		drawCircuito(); 
		/* Si el controlador no se encuentra parado, ejecuta un ciclo en el proceso evolutivo
		 * para que este pueda avanzar y realizar toda su lógica interna 
		 */
		if(!controlador.isParado()) {
			controlador.manipularPoblacion();
		} else if (controlador.getEstado() == Estado.PAUSADO) {
			/* Si el proceso se encuentra pausado, debe continuar mostrando las entidades que no
			 * hayan chocado aunque no estén en movimiento, para que no desaparezcan en pantalla */
			controlador.getVisualizador().mostrarEntidadesActivas();
		}
	}

	/**
	 * Evento que provoca que cuando pinchemos con el ratón en una de las entidades que se muestran
	 * en pantalla, se seleccione esa entidad y pueda pasar a monitorizarla en tiempo real. Se hace
	 * una llamada al controlador de ventos pasándole la posición del ratón para determinar cual
	 * de las entidades es la que el usuario ha seleccionado.
	 */
	public void mousePressed() {
		controlador.getControladorEventos().seleccionarEntidad(new PVector(mouseX, mouseY));
	}

	/**
	 * Evento que provoca que según la tecla que se haya pulsado, se llame en el controlador de 
	 * eventos el evento que le corresponde, de forma que realice la misma función que si 
	 * pulsaras en los botones del panel de control. 
	 */
	public void keyPressed() {
		/* Para la barra espaciadora tendrá que determinar el controlador qué evento debe
		 * realizar según las condiciones que se den y el estado del proceso, pudiendo 
		 * empezar, continuar, pausar o reanudar el proceso según la situación.
		 */
		if(key == ' ') {
			controlador.getControladorEventos().realizarEventoEspacio();
			return;
		}
		if(key == 'a' || key == 'A') {
			//Activa o desactiva el modo automático
			controlador.getControladorEventos().cambiarModo();
			return;
		}
		if(key == 'r' || key == 'R') {
			//Reinicia el proceso al estado inicial
			controlador.getControladorEventos().reiniciar();
			return;
		}
		if(key == 'd' || key == 'D') {
			/* Este es el único caso en el que no hace falta llamar a un evento del
			 * controlador, ya que lo que hace es activar o desactivar la flag del modo
			 * debug, para que cuando se muestren las entidades decida si tiene que dibujar
			 * sus flechas de dirección o no */
			modoDebug = !modoDebug;
			return;
		}
	}
	
	/**
	 * Dibuja la meta y los obstáculos del modelo, que están definidos por el circuito actual
	 */
	private void drawCircuito() {
		Modelo modelo = controlador.getModelo();
		drawMeta(modelo.getMeta());
		drawObstaculos(modelo.getObstaculos());
	}
	
	/**
	 * Dibuja la meta, que será una elipse, a partir de su posición y dimensiones
	 * @param meta
	 */
	private void drawMeta(Meta meta) {
		/* Se superpone una matriz de transformación para realizarlas únicamente sobre el
		 * elemento que vamos a dibujar y que las transformaciones no afecten a lo demás */
		pushMatrix();
		/* Trasladamos el eje de coordenadas hacia la posición de la meta, para que sea el
		 * punto de origen de coordenadas nuevo y podamos realizar el dibujado y otras 
		 * transformaciones desde ahí */
		translate(meta.getPosicion().x, meta.getPosicion().y);
		//Establecemos las propiedades con el que se dibujará la figura
        fill(0, 255, 0); 
        strokeWeight(4); 
        /* Indicamos que la posición desde donde debe dibujar la elipse es su centro, de forma
         * que si posicionamos la elipse en (0, 0), el centro está en el origen de coordenadas
         * y las dimensiones partirán desde ese punto.
         */
        ellipseMode(CENTER);
        /* Dibujamos una elipse en el nuevo origen de coordenadas después de trasladar los ejes
         * a la posición que debería tener la meta en la ventana. Las dimensiones, en este caso
         * los ejes de ela elipse, serán su ancho y alto
         */
        ellipse(0, 0, meta.getAncho(), meta.getAlto());
        /* Cuando se termine de transformar, sacamos la matriz de la pila para volver al 
         * contexto anterior, es decir, el sistema de coordenadas vuelva al origen de la
         * ventana y no se apliquen las transformaciones a lo que venga después */
        popMatrix();
        strokeWeight(1);
	}
	
	/**
	 * Dibuja cada uno de los obstáculo que tendrá el circuito
	 * @param obstaculos: colección de obstáculos
	 */
	private void drawObstaculos(Obstaculo[] obstaculos) {
		for(Obstaculo obs: obstaculos) {
			drawObstaculo(obs);
		}
	}
	
	/**
	 * Dibuja un obstáculo, que tendrá forma de rectángulo, con unas dimensiones y rotado
	 * en el ángulo que le haya sido asignado
	 * @param obstaculo
	 */
	private void drawObstaculo(Obstaculo obstaculo) {
		pushMatrix();
        stroke(0);
        fill(165);
        //Movemos el sistema de coordenadas a la posición donde debe estar el obstáculo
        translate(obstaculo.getPosicion().x, obstaculo.getPosicion().y);
        /* Aplicamos una transformación de rotación sobre los ejes de coordenadas, para 
         * que todo lo que dibujemos en esta matriz aparezca rotado en el ángulo indicado.
         * Esto es posible ya que si alteramos la disposición de los ejes, lo que se
         * encuentra dentro se representa de igual manera en relativo a estos, solo que 
         * el punto de vista desde fuera del sistema de coordenadas lo visualiza
         * en un ángulo distinto
         */
        rotate(obstaculo.getAngulo());
        //Dibujamos el rectángulo con su ancho y alto en el origen de coordenadas
        rectMode(CENTER);
        rect(0, 0, obstaculo.getAncho(), obstaculo.getAlto());
        popMatrix();
	}
	
	/**
	 * Dibuja una entidad con la forma pre-procesada que hemos definido al inicio,
	 * en la posición en la que se encuentra y mirando en la dirección que indique
	 * su vector de velocidad. 
	 * @param posicion en la que se encuentra la entidad
	 * @param velocidad con la que se desplaza la entidad
	 * @param monitorizada: si la entidad debe resaltarse con un contorno distinto para
	 * destacarla sobre el resto.
	 */
	public void drawEntidad(PVector posicion, PVector velocidad, boolean monitorizada) {
		pushMatrix();
		//Desplazamos el origen de coordenadas a la posición de la entidad
		translate(posicion.x, posicion.y);
		/* Rotamos el sistema de coordenadas en el ángulo determinado por su velocidad, 
		 * para que se visualice a la entidad orientada en la dirección en la que se está
		 * moviendo actualmente. El ángulo se obtiene a partir de obtener el arcotangente
		 * de la componente x e y del vector velocidad, que trigonométricamente equivale
		 * al ángulo entre el lado adyacente y opuesto del triángulo formado por las
		 * coordenadas del vector, dando como resultado la dirección y sentido de movimiento
		 */
		rotate(atan2(velocidad.y, velocidad.x));
		//Si está monitorizada resalta el borde de la figura con un color, si no lo deja en negro
		int colorStroke = monitorizada ? color(204, 0, 255) : color(0, 0, 0);
		humanoide.setStroke(colorStroke); //le asigna el color del borde 
		/* Dibuja la figura humanoide que hemos definido en la configuración inicial de la ventana
		 * que al estar pre-procesada no tendrá que programar de nuevo todas sus propiedades y 
		 * solo habrá que preocuparse de que represente la figura en la posición y rotación 
		 * que le corresponde a la entidad en ese momento, ya que todas tienen la misma apariencia
		 */
		shape(humanoide);
		/* Si el modo debug está activado, dibujará además una flecha de dirección que representa
		 * el movimiento que está realizando a partir de su vector de velocidad. Esto puede 
		 * ralentizar severamente el programa */
		if(modoDebug == true) {
			drawFlechaDireccion(velocidad);
		}
		popMatrix();
	}
	
	/**
	 * Dibuja una flecha de dirección que representa lo rápido que se está desplazando
	 * una entidad y en qué dirección y sentido lo está haciendo, a partir de su velocidad
	 * @param velocidad que determina el desplazamiento de la entidad
	 */
	private void drawFlechaDireccion(PVector velocidad) {
		/* La rapidez con la que se mueve la entidad viene determinada por la magnitud del vector
		 * de la velocidad, de forma que cuanto más alto sea su valor, más posiciones se desplaza
		 * en una dirección y viceversa. Como la velocidad tiene valores demasiado pequeños para 
		 * ser visualizados, se multiplica su magnitud por un valor arbitrario para que la magnitud
		 * se represente de manera más clara, sin perder fidelidad a la hora de distinguir cómo de
		 * rápido se mueve una entidad en comparación con otra. */
		float magnitudVelocidad = velocidad.mag() * 5;
		/* Se dibuja una forma de flecha a partir de líneas de color rojo. La longitud de esa flecha,
		 * es decir, la posición del punto final de la línea respecto al origen de coordenadas de la 
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
	 * colección de vectores que equivalen a todas las fuerzas que se le han aplicado para poder
	 * llegar en el número de frames indicado. Con esos genes podrá trazar las líneas a partir de
	 * simular los movimientos que ha ido realizando en cada frame
	 * @param ruta: todos los vectores de fuerzas que se han aplicado
	 * @param tiempoObtenido: el tiempo en frames que ha necesitado la entidad para llegar
	 * a la meta
	 */
	public void drawRutaOptima(PVector[] ruta, int tiempoObtenido) {
		//Se pone el "pincel" a rojo para pintar la linea de la ruta
		stroke(255,0,0);
		strokeWeight(3);
		//Almacena una copia del vector de la posición inicial de la entidad
		PVector posicion = controlador.getModelo().getPoblacion().getPosInicial().copy();
		//Dibuja una elipse que representa el punto inicial de la ruta
		ellipseMode(CENTER);
		fill(255);
		ellipse(posicion.x, posicion.y, 20, 20);
		//Inicializa los vectores de velocidad y aceleración para realizar el trazado
		PVector velocidad = new PVector(0,0);
		PVector aceleracion = new PVector(0,0);
		/* Por cada frame entre los que ha tardado en llegar a la meta (el tiempo obtenido),
		 * dibuja una línea entre una posición de origen y una final correspondiente al 
		 * movimiento que se ha realizado en ese frame. Para ello deberá simular el mismo
		 * desplazamiento */
		for(int i = 0; i < tiempoObtenido - 1; i++) {
			//Se le pasa como argumento todos los vectores de movimiento para que pueda trazar
			drawLineaRuta(ruta[i], posicion, velocidad, aceleracion);
		}
		strokeWeight(1);
	}

	/**
	 * Dibuja una línea desde un punto de origen a un punto final que simula el desplazamiento
	 * que ha realizado una entidad en un frame tras aplicarle la fuerza de un gen para 
	 * provocar su movimiento, que debemos simular para poder calcular el trazado 
	 * @param fuerza aplicada por el gen
	 * @param posicion antes de realizar el movimiento
	 * @param velocidad a la que se estaba moviendo antes de aplicarle la fuerza
	 * @param aceleracion a la que se le aplicará la fuerza
	 */
	private void drawLineaRuta(PVector fuerza, PVector posicion, PVector velocidad, PVector aceleracion) {
		/* Almacena una copia de la posición antes de simular el siguiente movimiento para
		 * poder trazar la línea entre la posición de origen y la posición final */
		PVector posicionPrevia = posicion.copy();
		//Realiza el mismo movimiento que la entidad en un frame modificando sus vectores 
		simularMovimiento(fuerza, posicion, velocidad, aceleracion);
		//Traza la línea entre la posición antes del movimiento y después del movimiento
		line(posicionPrevia.x, posicionPrevia.y, posicion.x, posicion.y);
		pushMatrix();
		//Solo deberá mostrar la flecha de dirección de velocidad si está activado el modo debug
		if(modoDebug) {
			drawFlechaDireccion(velocidad, posicionPrevia);
		}
		popMatrix();
		stroke(255,0,0);
		//Reinicia la aceleración a cero para preparar el siguiente movimiento
		aceleracion.mult(0);
	}
	
	/**
	 * Realiza el mismo movimiento que una entidad en un determinado frame a partir de la
	 * fuerza contenida en el gen correspondiente en ese momento. 
	 * @param fuerza aplicada por el gen
	 * @param posicion antes de realizar el movimiento
	 * @param velocidad a la que se estaba moviendo antes de aplicarle la fuerza
	 * @param aceleracion a la que se le aplicará la fuerza
	 */
	private void simularMovimiento(PVector fuerza, PVector posicion, PVector velocidad, PVector aceleracion) {
		aceleracion.add(fuerza);
		velocidad.add(aceleracion);
		posicion.add(velocidad);
	}
	
	/**
	 * Al igual que se haría cuando se activa el modo debug con las entidades activas, dibuja
	 * una flecha representando la dirección y rapidez con la que se ha desplazado desde la
	 * posición anterior a la actual, partiendo desde la posición de origen
	 * @param velocidad con la que se estaba moviendo en un frame en particular
	 * @param posicionPrevia en la que estaba antes de desplazarse con la velocidad
	 */
	private void drawFlechaDireccion(PVector velocidad, PVector posicionPrevia) {
		//Trasladamos el sistema de coordenadas a la posición anterior
		translate(posicionPrevia.x, posicionPrevia.y);
		//Rotamos en la dirección de la velocidad en este instante
		rotate(atan2(velocidad.y, velocidad.x));
		stroke(204, 0, 255);
		//Obtenemos su magnitud amplificada
		float magnitudVelocidad = velocidad.mag() * 5;
		//Dibujamos solo la punta de la flecha para que no tape la línea trazada de la ruta
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, -5);
		line(magnitudVelocidad, 0, magnitudVelocidad - 5, 5);
	}
	
	/**
	 * Crea un objeto PShape que representa una forma humanoide para las entidades. El proceso de
	 * crear la forma consistirá en crear componentes individuales con sus características propias
	 * y añadirlos a la forma que hace de contenedor como si fuera una agrupación de formas, para
	 * conseguir el aspecto que deseamos que se muestre
	 * @return el objeto que almacena la forma que tendrán todas las entidades
	 */
	private PShape crearFormaEntidad() {
		//Iniciamos una plantilla base para crear una forma y le indicamos que será una agrupación de formas
		PShape humanoide = createShape(GROUP);
		/* Creamos el "cuerpo" de la forma, que será un triángulo isósceles cuyo vértice más "alto" se
		 * encontrará en el origen de coordenadas de la entidad y el resto de vértices "bajos" se dibujarán 
		 * en el eje negativo horizontal. De esta forma, ese vértice alto coincide con la posición de 
		 * la entidad en el eje de coordenadas de la ventana, y por tanto, cada vez que rotemos la figura
		 * de la entidad, el triángulo apuntará en la dirección en la que se mueve esta en todo momento. */
		PShape cuerpo = createShape(TRIANGLE, -25, -10, 0, 0, -25, 10);
		//Podemos personalizar las propiedades individuales de este componente
		cuerpo.setFill(color(0, 0, 255)); 
	    cuerpo.setStroke(color(0));
	    cuerpo.setStrokeWeight(2);
		humanoide.addChild(cuerpo); //Añadimos la forma a la forma contenedor para que forme parte de ella
		/* Crea la "cabeza" de la forma, que será una elipse cuyo centro estará en el origen de coordenadas
		 * de la entidad y por tanto coincide con la posición de la entidad en ese instante. */
	    PShape cabeza = createShape(ELLIPSE, 0, 0, 10, 10);
	    cabeza.setFill(color(0, 0, 255)); 
	    cabeza.setStroke(color(0));
	    cabeza.setStrokeWeight(2);
	    humanoide.addChild(cabeza);
	    /* Crea las cuatro "extremidades" de la forma, cuyas formas son un conjunto de lineas delimitadas
	     * por unos vértices conectados entre sí. Las posiciones de esos vértices tienen una colocación
	     * deliberada sobre el eje de coordenadas de la entidad, para que parezca que estén conectadas
	     * al "cuerpo" de la figura y tenga el diseño de una especie de humanoide con piernas y con los
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
		/* Una vez tenemos todos los componentes creados y añadidos a la base, podemos devolver la forma
		 * entera funcionando como una sola figura que podremos llamar en cualquier momento y cuyas 
		 * propiedades ya estarán creadas y solo deberá cargarlas desde la caché del motor gráfico ahorrando
		 * tiempo de ejecución. Como las figuras funcionan como una unidad, si cambiamos las propiedades de
		 * esta o realizamos alguna transformación, se aplica al conjunto.
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
		//Le asignamos la cadena que debe mostrar y en que posición
		text("Framerate: " + round(frameRate), 10, 15); //Redondeamos el valor ya que es decimal
	}
	
	/**
	 * Dibuja un texto que muestra el estado actual del proceso evolutivo para mantener al usuario
	 * informado de en qué situación se encuentra tras interactuar con la interfaz. Su valor
	 * se actualiza desde el propio proceso cuando detecte que se ha cambiado de estado.
	 * @param estado en el que se encuentra
	 */
	private void drawEstado(Estado estado) {
		textSize(18);
		textAlign(LEFT, CENTER);
		fill(80); 
		text("Estado: ", 9, 45);
		/* El valor del estado se mostrará en el color que le ha sido asignado como propiedad a
		 * la constante del Enum correspondiente. De esta forma, cada uno de los estados tiene
		 * un color RGB que se podrá obtener para representarlos de una manera más diferenciada */
		int[] colorEstado = estado.getColor();
		//Coloreamos el texto del valor a partir de los valores RGB almacenados en el estado
		fill(colorEstado[0], colorEstado[1], colorEstado[2]); 
		//Mostramos el estado resaltado en el color adecuado
		text(estado.getTexto(), 9 + textWidth("Estado: "), 45);
	}
	
	/**
	 * Evento que se dispara cuando el usuario trata de redimensionar la ventana. A pesar
	 * de haber desactivado la posibilidad de hacerlo en la configuración de la ventana, el motor
	 * "P2D" de OpenGL que estamos utilizando tiene un problema en el que se sigue permitiendo
	 * maximizar la ventana desde la barra de título. Por ese motivo, si se trata de hacerlo,
	 * debe volverse a redimensionar inmediatamente al tamaño original y fijado establecido 
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
		 * alto de la ventana, nos queda la posición en la que tiene que encontrarse la ventana
		 * para mostrarse centrada vertical y/o horizontalmente sobre esta. Después de eso le
		 * añadimos 25 pixeles a la posición horizontal para que esté ligeramente desplazado del centro
		 */
		int posicionVentanaX = (displayWidth - width) / 2 + 25; 
		/* Se puede convertir pantalla/2 (punto medio pantalla) - ventana/2 (punto medio pantalla) en
		 * (pantalla - ventana) / 2 para obtener el punto centrado 
		 */
        int posicionVentanaY = (displayHeight - height) / 2;
        //Establecemos la posición de la superficie de la ventana
        surface.setLocation(posicionVentanaX, posicionVentanaY);
	}
	
	public int getNumFramesGen() {
		return numFramesGen;
	}

	public void setNumFramesGen(int numFramesGen) {
		this.numFramesGen = numFramesGen;
	}
	
}
