package modelo.entidades;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import controlador.Controlador;
import controlador.Visualizador;
import modelo.Modelo;
import processing.core.PVector;

/**
 * Agrupación de entidades que se encarga de iniciarlas con unos parámetros en común,
 * y de llevar a cabo el proceso evolutivo de éstas paso por paso
 * @author Alberto
 */
public class Poblacion {
	
	/**
	 * Modelo de datos que contiene el resto de elementos con los que debe interaccionar
	 * la población, así como el controlador para comunicarse con la interfaz
	 */
	private Modelo contexto;
	
	/**
	 * Colección de entidades que forman parte de la población
	 */
	private Entidad[] entidades;
	/**
	 * Colección que contiene entidades repetidas x veces de acuerdo a la probabilidad
	 * que tiene de reproducirse dependiendo de su aptitud
	 */
	private ArrayList<Entidad> poolGenetico;
	/** 
	 * Probabilidad de que un gen mute tras el cruce de entidades en la reproducción
	 */
	private double tasaMutacion;
	/**
	 * Número de frames que tienen las entidades para realizar sus funciones
	 */
	private int tiempoVida;
	/**
	 * Contador de generaciones que se han reproducido 
	 */
	private int numGeneraciones;
	/**
	 * Punto de "spawn" donde aparecen todas las entidades al inicio de cada generación
	 */
	private PVector posInicial;
	/**
	 * Tiempo más cercano al objetivo obtenido por alguna entidad a lo largo de todas las generaciones
	 */
	private int mejorTiempo;
	/**
	 * Flag que indica si el objetivo de las entidades se ha cumplido 
	 */
	private boolean objetivoCumplido;
	/**
	 * La entidad que más cerca se ha quedado de cumplir o de haber cumplido el objetivo
	 */
	private Entidad mejorEntidad;
	/**
	 * Total de colisiones que han tenido las entidades con los obstáculos
	 */
	private int numColisiones;
	/**
	 * Total de veces que han llegado las entidades a la meta
	 */
	private int numLlegadas;
	/**
	 * Total de colisiones que han tenido las entidades con los obstáculos 
	 */
	private int numColisionesActual;
	/**
	 * Total de veces que han llegado las entidades a la meta en esta generacion
	 */
	private int numLlegadasActual;
	/**
	 * Cantidad de entidades que tiene la poblacion
	 */
	private int numEntidades;
	/**
	 * Tiempo que tienen que alcanzar las entidades para terminar el proceso
	 */
	private int tiempoObjetivo = 140;
	/**
	 * Entidad que actualmente está siendo monitorizada en el panel de control
	 */
	private Entidad entidadMonitorizada;
	/**
	 * Mejor aptitud obtenida hasta ahora entre todas las generaciones
	 */
	private double mejorAptitud;
	
	private Random random = new Random();

	
	/**
	 * Constructor que inicializa la población a través de una serie de parámetros iniciales
	 * y crea las colecciones de datos necesarias
	 * @param contexto: el modelo de datos que contiene el resto de elementos del sistema
	 * @param poblacionParams: mapa con los parámetors que dictan el comportamiento de las entidades
	 * @param posInicial: posición en la que aparecen todas las entidades al inicio de su generación
	 */
	public Poblacion(Modelo contexto, HashMap<String, Integer> poblacionParams, PVector posInicial) {
		this.contexto = contexto;
		//Crea un array con tantas entidades como se indique desde el controlador
		numEntidades = poblacionParams.get("NumEntidades");
		entidades = new Entidad[numEntidades];
		//Inicia el pool genético vacío para rellenarlo cada vez que se seleccionen las entidades
		poolGenetico = new ArrayList<Entidad>();
		//La tasa de mutación viene como porcentaje así que se convierte a valor decimal
		this.tasaMutacion = ((double) poblacionParams.get("TasaMutacion")) / 100;
		this.tiempoObjetivo = poblacionParams.get("TiempoObjetivo");
		this.tiempoVida = poblacionParams.get("TiempoVida");
		this.posInicial = posInicial;
		//El "record" de tiempo irá bajando a partir del tiempo de vida, que es el peor resultado
		mejorTiempo = tiempoVida; 
		objetivoCumplido = false;
		numGeneraciones = 1; //Empieza como la primera generación
		generarPrimeraGen();
	}
	
	/**
	 * Inicializa tantas entidades como vengan establecidas en el tamaño de población
	 */
	private void generarPrimeraGen() {
		for (int i=0; i < entidades.length; i++) {
			/* Como la primera generación no es producto de un cruce, se le pasa null
			 * para que se inicialice un ADN nuevo con genes aleatorios
			 */
			entidades[i] = new Entidad(this, null, i);
		}
	}

	/**
	 * Ejecuta para todas las entidades la función que deben realizar en cada frame
	 * y llama al controlador para que de la orden de actualizarlas en la ventana gráfica
	 */
	public void realizarCiclo() {
		for(int i=0; i < entidades.length; i++) {
			//La entidad se encontrará en otra posición y mirando a otra dirección tras actuar
			entidades[i].actuar();
			//Si la entidad choca, no debe mostrarla (mejora considerablemente el rendimiento)
			if(!entidades[i].isHaChocado()) {
				//Le comunica a la vista que muestre la entidad a través del controlador
				contexto.getControlador().getVisualizador().mostrarEntidad(entidades[i]);
			} 
		}
	}
	
	/**
	 * Realiza todos los pasos del algoritmo genético para que las entidades evolucionen
	 * hasta que tras evaluarlas se compruebe que se ha completado el objetivo
	 */
	public void evolucionar() {
		//Selecciona las entidades para determinar el proceso de reproducción
		seleccionar();
		//Reinicia los contadores de llegadas y colisiones para la generación actual
		numLlegadasActual = 0;
		numColisionesActual = 0;
		/* Si no ha cumplido aún el objetivo, pasa a reproducir a las entidades para
		 * producir una nueva generación a partir de la anterior
		 */
		if(!objetivoCumplido) {	
			reproducir();	
		} 
	}
	
	/**
	 * Reemplaza la entidad que estaba siendo monitorizada por otra (si es que había una)
	 * @param entidadMonitorizada
	 */
	public void setEntidadMonitorizada(Entidad entidadMonitorizada) {
		if(this.entidadMonitorizada != null) { 
			this.entidadMonitorizada.setMonitorizada(false);
		}
		this.entidadMonitorizada = entidadMonitorizada;
	}
	
	/**
	 * Evalúa a todas las entidades, y si todavía ninguna cumple el objetivo, pasa
	 * a calcular la proporción de las aptitudes respecto a la mejor de la generación
	 * para poder determinar que probabilidad tienen de reproducirse.
	 */
	private void seleccionar() {
		poolGenetico.clear(); //Limpia el pool genético para que lo rellene en la reproducción
		double mejorAptitud = evaluarEntidades();
		/* Si tras evaluar alguna cumple el objetivo actualiza la flag y no continúa
		 * calculando sus probabilidades de reproducción al no ser necesario 
		 */
		if(comprobarObjetivo()) {
			objetivoCumplido = true;
			return;
		}
		//Determina según sus aptitudes y la mejor conseguida esta generación como se reproducirán
		calcProbabilidadReproduccion(mejorAptitud);
	}

	/** 
	 * Calcula la aptitud para todas las entidades y comprueba cuál es la mejor
	 * calificada y si se ha superado el tiempo record
	 * @return la mejor aptitud obtenida en la generación actual
	 */
	private double evaluarEntidades() {
		double mejorAptitud = 0.0;
		for(Entidad entidad : entidades) {
			mejorAptitud = evaluarEntidad(mejorAptitud, entidad);
		}
		//Si hay una entidad siendo monitorizada, muestra la aptitud evaluada para se vea en el panel
		if(entidadMonitorizada != null) {	
			contexto.getControlador().getVisualizador().actualizarPanel("AptitudEntidad", 
					redondearAptitud(entidadMonitorizada.getAptitud()));
		}
		return mejorAptitud;
	}
	
	/**
	 * Califica una entidad a través de su función de aptitud y determina si ha
	 * superado a la mejor de esta generación. También comprueba si ha superado
	 * algún record entre todas las generaciones
	 * @param mejorAptitud conseguida hasta el momento
	 * @param entidad que debe evaluar
	 * @return la mejor aptitud la haya superado o no
	 */
	private double evaluarEntidad(double mejorAptitud, Entidad entidad) {
		//Si la aptitud obtenida es mejor que alguna anterior, la sustituye
		if (entidad.evaluarAptitud() > mejorAptitud) {
			mejorAptitud = entidad.getAptitud();
			//Comprueba si ha superado algún record
			comprobarTiempoRecord(entidad);
			comprobarMejorAptitud(mejorAptitud);
		}
		return mejorAptitud;
	}

	/**
	 * Compara si el tiempo obtenido por una entidad que ha obtenido la mejor aptitud
	 * de la generación hasta el momento, ha superado el record de tiempo actual y 
	 * almacena el tiempo que ha conseguido.
	 * @param entidad cuyo tiempo debe ser comparado
	 */
	private void comprobarTiempoRecord(Entidad entidad) {
		Controlador controlador = contexto.getControlador();
		int tiempoObtenido = entidad.getTiempoObtenido();
		Visualizador visualizador = controlador.getVisualizador();
		visualizador.actualizarPanel("TiempoRecordActual", tiempoObtenido);
		if(entidad.getTiempoObtenido() <= mejorTiempo) {
			mejorTiempo = tiempoObtenido;
			mejorEntidad = entidad;
			// Muestra en el panel de control el nuevo record de tiempo obtenido 
			visualizador.actualizarPanel("TiempoRecord", mejorTiempo);
		} 
	}

	/**
	 * Compara si la aptitud obtenida por una entidad que ha obtenido la mejor aptitud
	 * de la generación hasta el momento, ha superado a la mejor aptitud de todas las
	 * generaciones, y lo almacena de ser así
	 * @param aptitud que debe ser comparada
	 */
	private void comprobarMejorAptitud(double aptitud) {
		Visualizador visualizador = contexto.getControlador().getVisualizador();
		visualizador.actualizarPanel("MejorAptitudActual", redondearAptitud(aptitud));
		if(aptitud > mejorAptitud) {
			mejorAptitud = aptitud;
			visualizador.actualizarPanel("MejorAptitud", redondearAptitud(mejorAptitud));
		}
	}
	
	/**
	 * Comprueba si se ha cumplido el objetivo de las entidades de llegar al meta
	 * del circuito en el tiempo establecido.
	 * @return si el mejor tiempo hasta el momento pasa el tiempo objetivo 
	 */
	private boolean comprobarObjetivo() {
		return mejorTiempo <= tiempoObjetivo;
	}
	
	/**
	 * Determina la probabilidad que tiene cada entidad de reproducirse con otra según
	 * la proporción entre su aptitud y la mejor aptitud
	 * @param mejorAptitud obtenida esta generación
	 */
	private void calcProbabilidadReproduccion(double mejorAptitud) {
		/* Normaliza la aptitud para todas las entidades de forma que que se obtenga
		 * un valor equivalente a la proporción entre su aptitud y la mejor de la generación
		 */
		for(Entidad entidad : entidades) {
			entidad.setAptitud(entidad.getAptitud() / mejorAptitud);
		}
		/* Los valores normalizados son las probabilidades que tiene cada entidad
		 * de reproducirse. Se multiplica por 100 para saber cuantas veces se debe
		 * añadir la entidad al pool genético 
		 */
		for(Entidad entidad : entidades) {
			int probabilidad = (int) (entidad.getAptitud() * 100);
			/* Funciona como un saco con canicas dentro. Suponiendo que tienes canicas
			 * de varios colores repetidos x veces, si metes la mano tienes tantas 
			 * probabilidades de sacar la de un color como veces aparezca en el saco.
			 * En este caso las veces que aparecen las entidades en el "saco" vendrán
			 * determinadas por su aptitud
			 */
			for(int i=0; i < probabilidad; i++) {
				poolGenetico.add(entidad); 
			}
		}
	}

	/**
	 * Realiza el proceso de reproducción de las entidades para producir la siguiente
	 * generación a partir de la anterior. Deberán generarse el número de
	 * entidades hijas que se indique para esa generación, y se obtendrán a partir de escoger
	 * aleatoriamente dos parientes del pool genético seleccionado anteriormente.
	 * Despúes se cruzará el ADN de los parientes con el algoritmo adecuado para 
	 * crear la entidad nueva con ese ADN asignado, y se le aplicarán unas posibles
	 * mutaciones aleatorias en su genotipo para aumentar la variabilidad de la población
	 */
	private void reproducir() {
		//Se crea una colección nueva de entidades con el número de entidades actual
		Entidad[] nuevaGeneracion = new Entidad[numEntidades];
		/* Se añaden nuevas entidades hijas a la colección creadas tras "reproducirse"
		 * dos parientes aleatorios del pool genético
		 */
		for(int i=0; i < nuevaGeneracion.length; i++) {
			crearEntidadHija(nuevaGeneracion, i);
		}
		entidades = nuevaGeneracion; //Se sustituyen las entidades actuales por las nuevas
		numGeneraciones++; //Se incrementa el contador de generaciones
	}

	/**
	 * Genera un nuevo objeto Entidad apartir del cruce de los genes de dos parientes
	 * aleatorios del pool genético y tras realizar una posible mutación
	 * @param nuevaGeneracion la colección de entidades que sustituirá a la anterior
	 * @param i el índice de la entidad que se va a crear
	 */
	private void crearEntidadHija(Entidad[] nuevaGeneracion, int i) {
		//El primer pariente se obtiene del primer indice aleatorio que escoge
		int indPariente1 = random.nextInt(poolGenetico.size());
		Entidad pariente1 = poolGenetico.get(indPariente1);
		//El segundo pariente deberá ser uno con una aptitud distinta al primero
		Entidad pariente2 = encontrarParienteDistinto(pariente1);
		//Se obtiene el ADN que tendrá el hijo tras cruzar el de ambos parientes
		ADN adnHijo = cruzarEntidades(pariente1, pariente2);
		mutar(adnHijo); //Se le aplican las mutaciones que surjan aleatoriamente
		/* Como creamos una nueva entidad a partir de un genotipo ya construido,
		 * le pasamos el ADN como argumento a su constructor, y ya no generará
		 * genes aleatoriamente (excluyendo aquellos que han mutado)
		 */
		nuevaGeneracion[i] = new Entidad(this, adnHijo, i);
	}
	
	/**
	 * Busca un pariente aleatorio en el pool genético cuya aptitud no sea igual a
	 * la del primer pariente escogido para reproducirse.
	 * Sirve para evitar que un pariente se reproduzca consigo mismo, ya que no tendría
	 * mucho sentido en este contexto, y para que no se cruce con alguno con la misma
	 * aptitud, puesto que eso disminuiría considerablemente la variación de genes en
	 * los hijos obtenidos para la siguiente generación, evitando que evolucionen
	 * @param pariente1: primer pariente que se ha elegido para cruzarse
	 * @return el segundo pariente que se cruzará con el primero
	 */
	private Entidad encontrarParienteDistinto(Entidad pariente1) {
		int indPariente2;
		/* Hasta que no salga un pariente con una aptitud distinta, continúa sacando
		 * parientes aleatoriamente del pool genético
		 */
		do {
			indPariente2 = random.nextInt(poolGenetico.size());
		} while(poolGenetico.get(indPariente2).getAptitud() == pariente1.getAptitud());
		//Una vez ha obtenido el que buscaba, puede devolver el pariente para que se crucen
		Entidad pariente2 = poolGenetico.get(indPariente2);
		return pariente2;
	}
	
	/**
	 * Produce un ADN a partir de juntar los genes de dos parientes haciendo
	 * una "tirada al aire de una moneda" con cada gen que deberá tener
	 * @param pariente1
	 * @param pariente2
	 * @return el ADN (genotipo) con los genes obtenidos del cruce
	 */
	private ADN cruzarEntidades(Entidad pariente1, Entidad pariente2) {
		//Inicia un array de vectores cuyo tamaño son los frames de vida de la entidad
		PVector[] genesHijo = new PVector[tiempoVida];
		//Obtiene los genes de ambos parientes
		PVector[] genesPariente1 = pariente1.getAdn().getGenes();
	    PVector[] genesPariente2 = pariente2.getAdn().getGenes();
	    /* El número de cruces que se realizarán dependerá de si el tiempo de vida de los padres
	     * es mayor o igual que el de la nueva generación o no. En el primer caso será simplemente
	     * equivalente al tiempo de vida, pero en el caso de que sea menor, no habrá suficientes
	     * cruces para rellenar sus genes, así que hará tantos cruces como la resta de la 
	     * diferencia entre estos haya, para evitar errores de elementos vacíos.
	     */
	    int difTiempoVida = tiempoVida - genesPariente1.length;
	    boolean parientesVivenIgualOMas = genesPariente1.length >= tiempoVida;
	    int numCruces = parientesVivenIgualOMas ? tiempoVida : tiempoVida - difTiempoVida;
	    /* Le asigna un gen a cada uno de los genes que puede obtener a partir de los cruces entre
	     * los parientes 
	     */
		for(int i=0; i < numCruces; i++) {
			genesHijo[i] = elegirGenes(genesPariente1[i], genesPariente2[i]);
		}
		/* Si los parientes viven menos que los hijos, debe rellenar los genes que sobran con
		 * genes aleatorios, ya que no quedan más cruces que hacer
		 */
		if(!parientesVivenIgualOMas) {
			rellenarGenesExtra(genesHijo, numCruces);
		}
		return new ADN(genesHijo);
	}

	/**
	 * Asigna a un gen del hijo que se está creando el gen correspondiente al de alguno de
	 * los dos parientes según el que toque aleatoriamente
	 * @param genPariente1 gen del primer pariente
	 * @param genPariente2 gen del segundo pariente
	 */
	private PVector elegirGenes(PVector genPariente1, PVector genPariente2) {
		/* En cada gen tendrá un 50% de posibilidades de elegir el de un pariente u otro.
		 * Funciona como si se tirara una moneda al aire para cada uno
		 */
		if (random.nextBoolean()) {
	        return genPariente1;
	    } else {
	        return genPariente2;
	    }
	}

	/**
	 * Rellena el resto de genes que faltan del genotipo de un hijo con genes aleatorios
	 * @param genesHijo: la coleccion de genes que tiene elementos por rellenar
	 * @param numCruces: los cruces que llegó a hacer hasta que no quedaban más genes
	 */
	private void rellenarGenesExtra(PVector[] genesHijo, int numCruces) {
		//Genera un ADN temporal que sirve para obtener los genes aleatorios
		ADN adnTemp = new ADN(tiempoVida);
		//Comienza el bucle donde dejó el anterior para los genes que faltan
		for(int i=numCruces; i < tiempoVida; i++) {
			genesHijo[i] = adnTemp.getGenes()[i]; //Rellena con un gen aleatorio
		}
	}
	
	/**
	 * Recorre todos los genes del ADN de una entidad y por cada uno existirá 
	 * la posibilidad (determinada por la probabilidad de la tasa de mutación)
	 * de que mute transformándose en un vector completamente aleatorio
	 * @param adnHijo que podrá ser alterado
	 */
	private void mutar(ADN adnHijo) {
		for(PVector gen : adnHijo.getGenes()) {
			if(random.nextDouble(1) < tasaMutacion) {
				adnHijo.generarGenAleatorio(gen);
			}
		}
	}
	
	/**
	 * Reduce el número de decimales en notación científica que muestra la aptitud
	 * para hacerlo más legible en la interfaz
	 * @param aptitud
	 * @return la aptitud redondeada mostrando solo 4 decimales extra
	 */
	private String redondearAptitud(double aptitud) {
		/* Muestra solo un número seguido de como mucho 3 decimales y la notación
		 * científica correspondiente según los decimales que tenga la aptitud */
		DecimalFormat df = new DecimalFormat("0.0##E0");
		return df.format(aptitud);
	}
	
	/**
	 * Incrementa el número de colisiones con obstáculos y lo muestra en el panel de control
	 */
	public void incrNumColisiones() {
		contexto.getControlador().getVisualizador().actualizarPanel("Colisiones", ++numColisiones);
	}

	/**
	 * Incrementa el número de llegadas a la meta y lo muestra en el panel de control
	 */
	public void incrNumLlegadas() {
		contexto.getControlador().getVisualizador().actualizarPanel("Metas", ++numLlegadas);
	}
	
	/**
	 * Incrementa el número de colisiones con obstáculos  de esta generacion y lo muestra en el panel de control
	 */
	public void incrNumColisionesActual() {
		contexto.getControlador().getVisualizador().actualizarPanel("ColisionesActual", ++numColisionesActual);
	}

	/**
	 * Incrementa el número de llegadas a la meta de esta generacion y lo muestra en el panel de control
	 */
	public void incrNumLlegadasActual() {
		contexto.getControlador().getVisualizador().actualizarPanel("MetasActual", ++numLlegadasActual);
	}
	
	public Entidad[] getEntidades() {
		return entidades;
	}

	public void setNumEntidades(int numEntidades) {
		this.numEntidades = numEntidades;
	}

	public void setTasaMutacion(double tasaMutacion) {
		this.tasaMutacion = tasaMutacion / 100;
	}

	public int getNumGeneraciones() {
		return numGeneraciones;
	}

	public PVector getPosInicial() {
		return posInicial;
	}

	public void setPosInicial(PVector posInicial) {
		this.posInicial = posInicial;
	}

	public int getTiempoVida() {
		return tiempoVida;
	}

	public void setTiempoVida(int tiempoVida) {
		this.tiempoVida = tiempoVida;
	}

	public Modelo getContexto() {
		return contexto;
	}

	public boolean isObjetivoCumplido() {
		return objetivoCumplido;
	}

	public Entidad getMejorEntidad() {
		return mejorEntidad;
	}

	public int getTiempoObjetivo() {
		return tiempoObjetivo;
	}

	public void setTiempoObjetivo(int tiempoObjetivo) {
		this.tiempoObjetivo = tiempoObjetivo;
	}

	public Entidad getEntidadMonitorizada() {
		return entidadMonitorizada;
	}
	
}
