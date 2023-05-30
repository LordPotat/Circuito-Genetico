package modelo.entidades;

import modelo.componentes.Meta;
import modelo.componentes.Obstaculo;
import processing.core.PVector;

/**
 * Elementos cuyo ciclo de vida consiste en desplazarse en el espacio de acuerdo a lo que dicta
 * su genotipo (el ADN), que contiene las fuerzas que se le aplicar�n. Una vez colisione con alg�n
 * elemento del circuito, parar� de moverse. Una vez termine su ciclo de vida, deber� evaluarse
 * su aptitud de acuerdo a si ha cumplido su objetivo, que es llegar a la meta en el tiempo indicado.
 * @author Alberto
 */
public class Entidad {
	
	/**
	 * Genotipo de la entidad
	 */
	private ADN adn;
	/**
	 * Qu� gen le toca aplicar para moverse
	 */
	private int genActual;
	
	private Poblacion poblacion;
	/**
	 * Par�metros que determinan su movimiento en el espacio
	 */
	private PVector posicion, velocidad, aceleracion;
	/**
	 * Flags que se activan cuando colisiona con la meta o con alg�n obst�culo
	 */
	private boolean haChocado, haLlegado;
	/**
	 * Calificaci�n que recibe tras evaluar c�mo de cerca que ha quedado de cumplir su objetivo
	 */
	private double aptitud;
	
	/**
	 * Distancia "record" m�nima a la que se ha quedado de la meta hasta el momento
	 */
	private double distanciaMinima;
	
	/**
	 * El tiempo que ha tardado en llegar a la meta, si es que ha llegado
	 */
	private int tiempoObtenido;
	
	/**
	 * Constructor que a partir de la poblaci�n, establece los par�metros iniciales de la
	 * entidad, como la posici�n inicial y la distancia m�nima. Seg�n reciba un ADN como argumento
	 * o no, se le asignar� el genotipo que le corresponde o se le crear� uno nuevo
	 * @param poblacion a la que pertenece
	 * @param adn: genotipo
	 */
	public Entidad(Poblacion poblacion, ADN adn) {
		this.poblacion = poblacion;
		//Se copia el vector de posici�n inicial que comparten todas las entidades de la poblaci�n
		posicion = poblacion.getPosInicial().copy();
		//Velocidad y aceleraci�n comienzan vac�as ya que se modificar�n seg�n se apliquen fuerzas
		velocidad = new PVector(0,0);
		aceleracion = new PVector(0,0);
		aptitud = genActual = 0;
		haChocado = haLlegado = false;
		//La primera distancia "record" es la distancia entre la meta y el punto inicial
		distanciaMinima = PVector.dist(posicion, poblacion.getContexto().getMeta().getPosicion());
		if(adn != null) {
			this.adn = adn;
		} else {
			/* En caso de no recibir ningun adn (s�lo si es la primera generaci�n que se crea) se genera 
			 * uno nuevo con tantos genes como tiempo de vida tengan las entidades
			 */
			this.adn = new ADN(poblacion.getTiempoVida());
		}
	}
	
	/**
	 * Ciclo de vida de una entidad. Mientras queden genes por utilizar, se obtiene el vector
	 * de fuerza que debe aplicarse en ese frame para desplazarse en el espacio, y comprueba
	 * si colisiona con alg�n elemento. De hacerlo (choca con obst�culo o llega a la meta),
	 * no contin�a desplaz�ndose. Si ha llegado a la meta, su tiempo obtenido para de contar,
	 * pero si ha chochado con un obst�culo sigue incrementando a pesar de no moverse
	 */
	public void actuar() {
		//Comprobar colisiones actualizar� las flags para saber si debe seguir movi�nose
		if (!haChocado && !haLlegado) {
			//Obtiene la fuerza a aplicarle a partir del gen de su adn que toca para ese frame
			PVector fuerzaGenetica = adn.getGenes()[genActual];
			genActual++; //Incrementa el gen actual para el siguiente frame
			desplazar(fuerzaGenetica); //Su desplazamiento depender� de la fuerza aplicada
			comprobarColisiones();
		} else if(haChocado) {
			tiempoObtenido++; 
		}
	}

	/**
	 * Mueve a la entidad de una posici�n a otra.
	 * @param fuerza que altera su aceleraci�n
	 */
	private void desplazar(PVector fuerza) {
		/* Seg�n la segunda ley de Newton, asumiendo que no hay masa a tener en cuenta,
		 * la aceleraci�n ser� igual a la fuerza aplicada en ese instante (frame),
		 * as� que le sumamos ese vector
		 */
		aceleracion.add(fuerza);
		/* La velocidad a la que se mueve la entidad ir� variando en cada frame, por tanto
		 * se le sumara el vector de aceleraci�n cada vez para alterar su direcci�n y sentido 
		 * (hacia d�nde apunta la entidad) y magnitud (c�mo de r�pido se mover� en esa direcci�n)
		 */
		velocidad.add(aceleracion);
		/* Para determinar en qu� posici�n acabar� tras moverse, se le suma el vector de la
		 * velocidad. C�mo de lejos se desplaza en ese frame depende de la magnitud, y hacia
		 * q�e punto, de la direcci�n.
		 */
		posicion.add(velocidad);
		/* Resetea la magnitud de su aceleraci�n a 0 para que no incremente cada frame.
		 * De no hacerlo, la velocidad se dispar� a valores exageradamente grandes ya que
		 * cada vez se mover� m�s r�pido, y queremos que se desplace poco a poco en cada frame.
		 * Esto se debe a que si seguimos sumando fuerzas a la aceleraci�n, se acumulan y por tanto
		 * la variaci�n de la velocidad crece cada vez al incrementar la aceleraci�n constantemente
		 */
		aceleracion.mult(0);
	}
	
	/**
	 * Comprueba si ha chocado con alg�n elemento del circuito
	 */
	private void comprobarColisiones() {
		//Si choca con la meta, actualiza la flag y no contin�a comprobando m�s colisiones
		if(colisionaConMeta()) {
			haLlegado = true;
			return;
		}
		//Si no ha chocado con la meta, pasa a comprobar si ha chocado con alguno de los obst�culos
		for (Obstaculo obstaculo : poblacion.getContexto().getObstaculos()) {
			if(obstaculo.chocaConEntidad(posicion)) {
				//Actualiza la flag y termina de comprobar cuando encuentra una colisi�n
				haChocado = true; 
				break;
			}
		}
		//Incrementa el tiempo obtenido para este frame antes de que la flag tenga efecto
		tiempoObtenido++;
	}
	
	/**
	 * Comprueba si ha chocado con la meta del circuito y actualiza la distancia m�nima a �sta
	 * si es que se ha superado el "record"
	 * @return si existe una colisi�n o no con la meta
	 */
	private boolean colisionaConMeta() {
		Meta meta = poblacion.getContexto().getMeta();
		//Obtiene la distancia de la posici�n de la meta a la de la entidad
		double distancia = PVector.dist(posicion, meta.getPosicion());
		//Si la destancia es menor a la del "record", actualiza su valor
		if(distancia < distanciaMinima) {
			distanciaMinima = distancia;
		}
		//Realiza la comprobaci�n de la colisi�n para devolver el resultado
		return meta.chocaConEntidad(posicion) ? true : false;
	}
	
	/**
	 * Califica a la entidad actualizando su valor de aptitud dependiendo de lo cerca que
	 * se ha quedado de cumplir el objetivo de llegar a la meta en el tiempo indicado
	 * @return la aptitud calculada a partir de las f�rmulas
	 */
	public double evaluarAptitud() {
		normalizarDistanciaMinima();
		double factorTiempo = 1.0;
		/* La aptitud debe ser directamente proporcional a lo cerca que est� que est� el tiempo
		 * obtenido del tiempo objetivo. El factor de la f�rmula viene determinado por tanto
		 * por el tiempo objetivo entre el tiempo obtenido, para calcular la proporci�n
		 */
		factorTiempo = (double)poblacion.getContexto().getCircuito().getTiempoObjetivo() 
				/ (double)tiempoObtenido;
		/* Para optimizar los resultados, se eleva el factor a una potencia de 1 partido por
		 * un valor (como el factor es un decimal por debajo del 1, si queremos incrementar 
		 * su valor el exponente tambi�n debe ser menor que 1). 
		 * Tras numerosas pruebas el exponente que rinde mejor para recompensar el obtener un 
		 * tiempo m�s cercano al objetivo, sin que ello provoque una situaci�n de estancamiento 
		 * por premiarlo demasiado y no promover variedad, es el de 1/4, que es el punto de equilibrio
		 */
		factorTiempo = Math.pow(factorTiempo,1/4);
		/* La aptitud debe ser inversamente proporcional a la distancia minima a la meta
		 * que alcanza, y al tiempo obtenido. La f�rmula resultante ser� el factor de tiempo
		 * dividido entre tiempo obtenido por distancia m�nima. */
		aptitud = factorTiempo / (tiempoObtenido * distanciaMinima);
		//Si ha llegado a la meta, debe verse recompensado y por tanto mutiplica su aptitud
		if (haLlegado) {
			aptitud *= 4; //Tras realizar pruebas, 4 es el m�ltiplo que obtiene mejores resultados
		}
		/* Puede ser razonable pensar que har�a falta penalizar a los que se chocan, sin embargo,
		 * eso solo provocar�a menos variedad de entidades y terminar�a estancando la evoluci�n
		 * al ser much�simo m�s probable que solo se reproduzcan los que llegan a la meta. Eso
		 * favorece el elitismo y es algo que queremos evitar para conseguir mejores tiempos
		 */
		return aptitud;
	}

	/**
	 * Iguala la distancia m�nima a un mismo valor para cualquier entidad que llegue a la meta
	 */
	private void normalizarDistanciaMinima() {
		/* Como la entidad se desplaza de un punto a otro en el espacio, al colisionar con la meta
		 * lo m�s probable es que no est� en el borde, si no en alg�n punto dentro de su elipse.
		 * Por tanto, se debe igualar toda distancia que menor que la del borde al centro
		 * de la elipse al radio de �sta, para que todos los que llegan a la meta tengan
		 * la misma distancia m�nima sin importar en qu� punto exacto se paren.
		 */
		float radioMeta = poblacion.getContexto().getMeta().getAlto() / 2;
		if (distanciaMinima < radioMeta) {
			distanciaMinima = radioMeta;
		}
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

	public double getDistanciaMinima() {
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
	
	
}
