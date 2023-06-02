package modelo.entidades;

import modelo.circuito.Meta;
import modelo.circuito.Obstaculo;
import processing.core.PMatrix2D;
import processing.core.PVector;

/**
 * Elementos cuyo ciclo de vida consiste en desplazarse en el espacio de acuerdo a lo que dicta
 * su genotipo (el ADN), que contiene las fuerzas que se le aplicarán. Una vez colisione con algún
 * elemento del circuito, parará de moverse. Una vez termine su ciclo de vida, deberá evaluarse
 * su aptitud de acuerdo a si ha cumplido su objetivo, que es llegar a la meta en el tiempo indicado.
 * @author Alberto
 */
public class Entidad {
	
	/**
	 * Genotipo de la entidad
	 */
	private ADN adn;
	/**
	 * Qué gen le toca aplicar para moverse
	 */
	private int genActual;
	
	private Poblacion poblacion;
	/**
	 * Parámetros que determinan su movimiento en el espacio
	 */
	private PVector posicion, velocidad, aceleracion;
	/**
	 * Flags que se activan cuando colisiona con la meta o con algún obstáculo
	 */
	private boolean haChocado, haLlegado;
	/**
	 * Calificación que recibe tras evaluar cómo de cerca que ha quedado de cumplir su objetivo
	 */
	private double aptitud;
	
	/**
	 * Distancia "record" mínima a la que se ha quedado de la meta hasta el momento
	 */
	private float distanciaMinima;
	
	/**
	 * El tiempo que ha tardado en llegar a la meta, si es que ha llegado
	 */
	private int tiempoObtenido;
	/**
	 * Flag que indica si la entidad está siendo monitorizada en el panel de control
	 */
	private boolean monitorizada = false;
	/**
	 * Indice que ocupa en la población. Actua como si fuera un z_index al indicar
	 * cómo de superpuesta está respecto al resto de entidades 
	 */
	private int indice;
	/**
	 * Distancia a la que se encuentra de la meta en en frame actual
	 */
	private float distancia;
	
	/**
	 * Constructor que a partir de la población, establece los parámetros iniciales de la
	 * entidad, como la posición inicial y la distancia mínima. Según reciba un ADN como argumento
	 * o no, se le asignará el genotipo que le corresponde o se le creará uno nuevo
	 * @param poblacion a la que pertenece
	 * @param adn: genotipo
	 */
	public Entidad(Poblacion poblacion, ADN adn, int indice) {
		this.poblacion = poblacion;
		this.indice = indice;
		//Se copia el vector de posición inicial que comparten todas las entidades de la población
		posicion = poblacion.getPosInicial().copy();
		//Velocidad y aceleración comienzan vacías ya que se modificarán según se apliquen fuerzas
		velocidad = new PVector(0,0);
		aceleracion = new PVector(0,0);
		aptitud = genActual = 0;
		tiempoObtenido = 0;
		haChocado = haLlegado = false;
		//La primera distancia "record" es la distancia entre la meta y el punto inicial
		distanciaMinima = PVector.dist(posicion, poblacion.getContexto().getMeta().getPosicion());
		if(adn != null) {
			this.adn = adn;
		} else {
			/* En caso de no recibir ningun adn (sólo si es la primera generación que se crea) se genera 
			 * uno nuevo con tantos genes como tiempo de vida tengan las entidades
			 */
			this.adn = new ADN(poblacion.getTiempoVida());
		}
	}
	
	/**
	 * Ciclo de vida de una entidad. Mientras queden genes por utilizar, se obtiene el vector
	 * de fuerza que debe aplicarse en ese frame para desplazarse en el espacio, y comprueba
	 * si colisiona con algún elemento. De hacerlo (choca con obstáculo o llega a la meta),
	 * no continúa desplazándose. Si ha llegado a la meta, su tiempo obtenido para de contar,
	 * pero si ha chochado con un obstáculo sigue incrementando a pesar de no moverse
	 */
	public void actuar() {
		//Comprobar colisiones actualizará las flags para saber si debe seguir moviénose
		if (!haChocado && !haLlegado) {
			//Obtiene la fuerza a aplicarle a partir del gen de su adn que toca para ese frame
			PVector fuerzaGenetica = adn.getGenes()[genActual];
			genActual++; //Incrementa el gen actual para el siguiente frame
			desplazar(fuerzaGenetica); //Su desplazamiento dependerá de la fuerza aplicada
			comprobarColisiones();
		} else if(haChocado) {
			tiempoObtenido++; 
		}
	}

	/**
	 * Mueve a la entidad de una posición a otra.
	 * @param fuerza que altera su aceleración
	 */
	private void desplazar(PVector fuerza) {
		/* Resetea la magnitud de su aceleración a 0 para que no incremente cada frame.
		 * De no hacerlo, la velocidad se dispará a valores exageradamente grandes ya que
		 * cada vez se moverá más rápido, y queremos que se desplace poco a poco en cada frame.
		 * Esto se debe a que si seguimos sumando fuerzas a la aceleración, se acumulan y por tanto
		 * la variación de la velocidad crece cada vez al incrementar la aceleración constantemente
		 */
		aceleracion.mult(0);
		/* Según la segunda ley de Newton, asumiendo que no hay masa a tener en cuenta,
		 * la aceleración será igual a la fuerza aplicada en ese instante (frame),
		 * así que le sumamos ese vector
		 */
		aceleracion.add(fuerza);
		/* La velocidad a la que se mueve la entidad irá variando en cada frame, por tanto
		 * se le sumara el vector de aceleración cada vez para alterar su dirección y sentido 
		 * (hacia dónde apunta la entidad) y magnitud (cómo de rápido se moverá en esa dirección)
		 */
		velocidad.add(aceleracion);
		/* Para determinar en qué posición acabará tras moverse, se le suma el vector de la
		 * velocidad. Cómo de lejos se desplaza en ese frame depende de la magnitud, y hacia
		 * qúe punto, de la dirección.
		 */
		posicion.add(velocidad);
	}
	
	/**
	 * Comprueba si ha chocado con algún elemento del circuito
	 */
	private void comprobarColisiones() {
		//Si choca con la meta, actualiza la flag y no continúa comprobando más colisiones
		if(colisionaConMeta()) {
			//Incrementa el contador de llegadas a la meta de la población
			poblacion.incrNumLlegadas();
			poblacion.incrNumLlegadasActual();
			haLlegado = true;
			return;
		}
		//Si no ha chocado con la meta, pasa a comprobar si ha chocado con alguno de los obstáculos
		for (Obstaculo obstaculo : poblacion.getContexto().getObstaculos()) {
			if(obstaculo.chocaConEntidad(posicion)) {
				//Incrementa el contador de colisiones de la población
				poblacion.incrNumColisiones();
				poblacion.incrNumColisionesActual();
				//Actualiza la flag y termina de comprobar cuando encuentra una colisión
				haChocado = true; 
				break;
			}
		}
		//Incrementa el tiempo obtenido para este frame antes de que la flag tenga efecto
		tiempoObtenido++;
	}
	
	/**
	 * Comprueba si ha chocado con la meta del circuito y actualiza la distancia mínima a ésta
	 * si es que se ha superado el "record"
	 * @return si existe una colisión o no con la meta
	 */
	private boolean colisionaConMeta() {
		Meta meta = poblacion.getContexto().getMeta();
		//Obtiene la distancia de la posición de la meta a la de la entidad
		distancia = PVector.dist(posicion, meta.getPosicion());
		//Si la destancia es menor a la del "record", actualiza su valor
		if(distancia < distanciaMinima) {
			distanciaMinima = distancia;
		}
		//Realiza la comprobación de la colisión para devolver el resultado
		return meta.chocaConEntidad(posicion) ? true : false;
	}
	
	/**
	 * Califica a la entidad actualizando su valor de aptitud dependiendo de lo cerca que
	 * se ha quedado de cumplir el objetivo de llegar a la meta en el tiempo indicado
	 * @return la aptitud calculada a partir de las fórmulas
	 */
	public double evaluarAptitud() {
		normalizarDistanciaMinima();
		double factorTiempo = 1.0;
		/* La aptitud debe ser directamente proporcional a lo cerca que está que está el tiempo
		 * obtenido del tiempo objetivo. El factor de la fórmula viene determinado por tanto
		 * por el tiempo objetivo entre el tiempo obtenido, para calcular la proporción
		 */
		factorTiempo = (double)poblacion.getTiempoObjetivo() / (double)tiempoObtenido;
		/* Para optimizar los resultados, se eleva el factor a una potencia de 1 partido por
		 * un valor (como el factor es un decimal por debajo del 1, si queremos incrementar 
		 * su valor el exponente también debe ser menor que 1). 
		 * Tras numerosas pruebas el exponente que rinde mejor para recompensar el obtener un 
		 * tiempo más cercano al objetivo, sin que ello provoque una situación de estancamiento 
		 * por premiarlo demasiado y no promover variedad, es el de 1/4, que es el punto de equilibrio
		 */
		factorTiempo = Math.pow(factorTiempo,1/4);
		/* La aptitud debe ser inversamente proporcional a la distancia minima a la meta
		 * que alcanza, y al tiempo obtenido. La fórmula resultante será el factor de tiempo
		 * dividido entre tiempo obtenido por distancia mínima. */
		aptitud = factorTiempo / (tiempoObtenido * distanciaMinima);
		//Si ha llegado a la meta, debe verse recompensado y por tanto mutiplica su aptitud
		if (haLlegado) {
			aptitud *= 4; //Tras realizar pruebas, 4 es el múltiplo que obtiene mejores resultados
		}
		/* Puede ser razonable pensar que haría falta penalizar a los que se chocan, sin embargo,
		 * eso solo provocaría menos variedad de entidades y terminaría estancando la evolución
		 * al ser muchísimo más probable que solo se reproduzcan los que llegan a la meta. Eso
		 * favorece el elitismo y es algo que queremos evitar para conseguir mejores tiempos
		 */
		return aptitud;
	}

	/**
	 * Iguala la distancia mínima a un mismo valor para cualquier entidad que llegue a la meta
	 */
	private void normalizarDistanciaMinima() {
		/* Como la entidad se desplaza de un punto a otro en el espacio, al colisionar con la meta
		 * lo más probable es que no esté en el borde, si no en algún punto dentro de su elipse.
		 * Por tanto, se debe igualar toda distancia que menor que la del borde al centro
		 * de la elipse al radio de ésta, para que todos los que llegan a la meta tengan
		 * la misma distancia mínima sin importar en qué punto exacto se paren.
		 */
		float radioMeta = poblacion.getContexto().getMeta().getAlto() / 2;
		if (distanciaMinima < radioMeta) {
			distanciaMinima = radioMeta;
		}
	}
	
	/**
	 * Comprueba si la posicion del raton está dentro de la hitbox de la entidad.
	 * El algoritmo de colisión es muy parecido al de los obstáculos.
	 * @param posRaton
	 * @return si la hitbox contiene donde se encuentra el ratón
	 */
	public boolean contieneRaton(PVector posRaton) {
		
		//Distancia relativa del ratón a la entidad
		PVector posRelativa = PVector.sub(posRaton, posicion);
		
		//Obtener matrix rotacion entidad
	    PMatrix2D matrizRotacion = new PMatrix2D(); 
	    float angulo = (float) Math.atan2(velocidad.x, velocidad.y); 
	    matrizRotacion.rotate(-angulo);
	    
	    //Punto relativo con la rotación revetida
	    PVector posRotada = new PVector();
	    posRotada.x = posRelativa.x * matrizRotacion.m00 + posRelativa.y * matrizRotacion.m01;
	    posRotada.y = posRelativa.x * matrizRotacion.m10 + posRelativa.y * matrizRotacion.m11;
	    
	    /* Puntos para comprobar si se encuentra en la "hitbox". Los valores escogidos
	     * se basan en la representación gráfica de la entidad más un margen para que
	     * la hitbox no sea muy pequeña y que no sea tan complicado acertar
	     */
	    float verticeX0 = -50;
	    float verticeX1 = 25;
	    float verticeY0 = -25;
	    float verticeY1 = 25;
	    
	    //Comprueba si se encuentra en la hitbox en ambos ejes
	    boolean colisionaX = posRotada.x >= verticeX0 && posRotada.x <= verticeX1; 
	    boolean colisionaY = posRotada.y >= verticeY0 && posRotada.y <= verticeY1;
	    return colisionaX && colisionaY;
	}
	
	public ADN getAdn() {
		return adn;
	}

	public double getAptitud() {
		return aptitud;
	}

	public PVector getPosicion() {
		return posicion;
	}

	public PVector getVelocidad() {
		return velocidad;
	}

	public PVector getAceleracion() {
		return aceleracion;
	}

	public float getDistanciaMinima() {
		return distanciaMinima;
	}

	public void setAptitud(double aptitud) {
		this.aptitud = aptitud;
	}

	public int getTiempoObtenido() {
		return tiempoObtenido;
	}

	public boolean isHaChocado() {
		return haChocado;
	}

	public boolean isHaLlegado() {
		return haLlegado;
	}

	public boolean isMonitorizada() {
		return monitorizada;
	}

	public void setMonitorizada(boolean monitorizado) {
		this.monitorizada = monitorizado;
	}

	public int getIndice() {
		return indice;
	}

	public float getDistancia() {
		return distancia;
	}

}