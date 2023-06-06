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
 * Agrupaci�n de entidades que se encarga de iniciarlas con unos par�metros en com�n,
 * y de llevar a cabo el proceso evolutivo de �stas paso por paso
 * @author Alberto
 */
public class Poblacion {
	
	/**
	 * Modelo de datos que contiene el resto de elementos con los que debe interaccionar
	 * la poblaci�n, as� como el controlador para comunicarse con la interfaz
	 */
	private Modelo contexto;
	
	/**
	 * Colecci�n de entidades que forman parte de la poblaci�n
	 */
	private Entidad[] entidades;
	/**
	 * Colecci�n que contiene entidades repetidas x veces de acuerdo a la probabilidad
	 * que tiene de reproducirse dependiendo de su aptitud
	 */
	private ArrayList<Entidad> poolGenetico;
	/** 
	 * Probabilidad de que un gen mute tras el cruce de entidades en la reproducci�n
	 */
	private double tasaMutacion;
	/**
	 * N�mero de frames que tienen las entidades para realizar sus funciones
	 */
	private int tiempoVida;
	/**
	 * Contador de generaciones que se han reproducido 
	 */
	private int numGeneraciones;
	/**
	 * Punto de "spawn" donde aparecen todas las entidades al inicio de cada generaci�n
	 */
	private PVector posInicial;
	/**
	 * Tiempo m�s cercano al objetivo obtenido por alguna entidad a lo largo de todas las generaciones
	 */
	private int mejorTiempo;
	/**
	 * Flag que indica si el objetivo de las entidades se ha cumplido 
	 */
	private boolean objetivoCumplido;
	/**
	 * La entidad que m�s cerca se ha quedado de cumplir o de haber cumplido el objetivo
	 */
	private Entidad mejorEntidad;
	/**
	 * Total de colisiones que han tenido las entidades con los obst�culos
	 */
	private int numColisiones;
	/**
	 * Total de veces que han llegado las entidades a la meta
	 */
	private int numLlegadas;
	/**
	 * Total de colisiones que han tenido las entidades con los obst�culos 
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
	 * Entidad que actualmente est� siendo monitorizada en el panel de control
	 */
	private Entidad entidadMonitorizada;
	/**
	 * Mejor aptitud obtenida hasta ahora entre todas las generaciones
	 */
	private double mejorAptitud;
	
	private Random random = new Random();

	
	/**
	 * Constructor que inicializa la poblaci�n a trav�s de una serie de par�metros iniciales
	 * y crea las colecciones de datos necesarias
	 * @param contexto: el modelo de datos que contiene el resto de elementos del sistema
	 * @param poblacionParams: mapa con los par�metors que dictan el comportamiento de las entidades
	 * @param posInicial: posici�n en la que aparecen todas las entidades al inicio de su generaci�n
	 */
	public Poblacion(Modelo contexto, HashMap<String, Integer> poblacionParams, PVector posInicial) {
		this.contexto = contexto;
		//Crea un array con tantas entidades como se indique desde el controlador
		numEntidades = poblacionParams.get("NumEntidades");
		entidades = new Entidad[numEntidades];
		//Inicia el pool gen�tico vac�o para rellenarlo cada vez que se seleccionen las entidades
		poolGenetico = new ArrayList<Entidad>();
		//La tasa de mutaci�n viene como porcentaje as� que se convierte a valor decimal
		this.tasaMutacion = ((double) poblacionParams.get("TasaMutacion")) / 100;
		this.tiempoObjetivo = poblacionParams.get("TiempoObjetivo");
		this.tiempoVida = poblacionParams.get("TiempoVida");
		this.posInicial = posInicial;
		//El "record" de tiempo ir� bajando a partir del tiempo de vida, que es el peor resultado
		mejorTiempo = tiempoVida; 
		objetivoCumplido = false;
		numGeneraciones = 1; //Empieza como la primera generaci�n
		generarPrimeraGen();
	}
	
	/**
	 * Inicializa tantas entidades como vengan establecidas en el tama�o de poblaci�n
	 */
	private void generarPrimeraGen() {
		for (int i=0; i < entidades.length; i++) {
			/* Como la primera generaci�n no es producto de un cruce, se le pasa null
			 * para que se inicialice un ADN nuevo con genes aleatorios
			 */
			entidades[i] = new Entidad(this, null, i);
		}
	}

	/**
	 * Ejecuta para todas las entidades la funci�n que deben realizar en cada frame
	 * y llama al controlador para que de la orden de actualizarlas en la ventana gr�fica
	 */
	public void realizarCiclo() {
		for(int i=0; i < entidades.length; i++) {
			//La entidad se encontrar� en otra posici�n y mirando a otra direcci�n tras actuar
			entidades[i].actuar();
			//Si la entidad choca, no debe mostrarla (mejora considerablemente el rendimiento)
			if(!entidades[i].isHaChocado()) {
				//Le comunica a la vista que muestre la entidad a trav�s del controlador
				contexto.getControlador().getVisualizador().mostrarEntidad(entidades[i]);
			} 
		}
	}
	
	/**
	 * Realiza todos los pasos del algoritmo gen�tico para que las entidades evolucionen
	 * hasta que tras evaluarlas se compruebe que se ha completado el objetivo
	 */
	public void evolucionar() {
		//Selecciona las entidades para determinar el proceso de reproducci�n
		seleccionar();
		//Reinicia los contadores de llegadas y colisiones para la generaci�n actual
		numLlegadasActual = 0;
		numColisionesActual = 0;
		/* Si no ha cumplido a�n el objetivo, pasa a reproducir a las entidades para
		 * producir una nueva generaci�n a partir de la anterior
		 */
		if(!objetivoCumplido) {	
			reproducir();	
		} 
	}
	
	/**
	 * Reemplaza la entidad que estaba siendo monitorizada por otra (si es que hab�a una)
	 * @param entidadMonitorizada
	 */
	public void setEntidadMonitorizada(Entidad entidadMonitorizada) {
		if(this.entidadMonitorizada != null) { 
			this.entidadMonitorizada.setMonitorizada(false);
		}
		this.entidadMonitorizada = entidadMonitorizada;
	}
	
	/**
	 * Eval�a a todas las entidades, y si todav�a ninguna cumple el objetivo, pasa
	 * a calcular la proporci�n de las aptitudes respecto a la mejor de la generaci�n
	 * para poder determinar que probabilidad tienen de reproducirse.
	 */
	private void seleccionar() {
		poolGenetico.clear(); //Limpia el pool gen�tico para que lo rellene en la reproducci�n
		double mejorAptitud = evaluarEntidades();
		/* Si tras evaluar alguna cumple el objetivo actualiza la flag y no contin�a
		 * calculando sus probabilidades de reproducci�n al no ser necesario 
		 */
		if(comprobarObjetivo()) {
			objetivoCumplido = true;
			return;
		}
		//Determina seg�n sus aptitudes y la mejor conseguida esta generaci�n como se reproducir�n
		calcProbabilidadReproduccion(mejorAptitud);
	}

	/** 
	 * Calcula la aptitud para todas las entidades y comprueba cu�l es la mejor
	 * calificada y si se ha superado el tiempo record
	 * @return la mejor aptitud obtenida en la generaci�n actual
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
	 * Califica una entidad a trav�s de su funci�n de aptitud y determina si ha
	 * superado a la mejor de esta generaci�n. Tambi�n comprueba si ha superado
	 * alg�n record entre todas las generaciones
	 * @param mejorAptitud conseguida hasta el momento
	 * @param entidad que debe evaluar
	 * @return la mejor aptitud la haya superado o no
	 */
	private double evaluarEntidad(double mejorAptitud, Entidad entidad) {
		//Si la aptitud obtenida es mejor que alguna anterior, la sustituye
		if (entidad.evaluarAptitud() > mejorAptitud) {
			mejorAptitud = entidad.getAptitud();
			//Comprueba si ha superado alg�n record
			comprobarTiempoRecord(entidad);
			comprobarMejorAptitud(mejorAptitud);
		}
		return mejorAptitud;
	}

	/**
	 * Compara si el tiempo obtenido por una entidad que ha obtenido la mejor aptitud
	 * de la generaci�n hasta el momento, ha superado el record de tiempo actual y 
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
	 * de la generaci�n hasta el momento, ha superado a la mejor aptitud de todas las
	 * generaciones, y lo almacena de ser as�
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
	 * Determina la probabilidad que tiene cada entidad de reproducirse con otra seg�n
	 * la proporci�n entre su aptitud y la mejor aptitud
	 * @param mejorAptitud obtenida esta generaci�n
	 */
	private void calcProbabilidadReproduccion(double mejorAptitud) {
		/* Normaliza la aptitud para todas las entidades de forma que que se obtenga
		 * un valor equivalente a la proporci�n entre su aptitud y la mejor de la generaci�n
		 */
		for(Entidad entidad : entidades) {
			entidad.setAptitud(entidad.getAptitud() / mejorAptitud);
		}
		/* Los valores normalizados son las probabilidades que tiene cada entidad
		 * de reproducirse. Se multiplica por 100 para saber cuantas veces se debe
		 * a�adir la entidad al pool gen�tico 
		 */
		for(Entidad entidad : entidades) {
			int probabilidad = (int) (entidad.getAptitud() * 100);
			/* Funciona como un saco con canicas dentro. Suponiendo que tienes canicas
			 * de varios colores repetidos x veces, si metes la mano tienes tantas 
			 * probabilidades de sacar la de un color como veces aparezca en el saco.
			 * En este caso las veces que aparecen las entidades en el "saco" vendr�n
			 * determinadas por su aptitud
			 */
			for(int i=0; i < probabilidad; i++) {
				poolGenetico.add(entidad); 
			}
		}
	}

	/**
	 * Realiza el proceso de reproducci�n de las entidades para producir la siguiente
	 * generaci�n a partir de la anterior. Deber�n generarse el n�mero de
	 * entidades hijas que se indique para esa generaci�n, y se obtendr�n a partir de escoger
	 * aleatoriamente dos parientes del pool gen�tico seleccionado anteriormente.
	 * Desp�es se cruzar� el ADN de los parientes con el algoritmo adecuado para 
	 * crear la entidad nueva con ese ADN asignado, y se le aplicar�n unas posibles
	 * mutaciones aleatorias en su genotipo para aumentar la variabilidad de la poblaci�n
	 */
	private void reproducir() {
		//Se crea una colecci�n nueva de entidades con el n�mero de entidades actual
		Entidad[] nuevaGeneracion = new Entidad[numEntidades];
		/* Se a�aden nuevas entidades hijas a la colecci�n creadas tras "reproducirse"
		 * dos parientes aleatorios del pool gen�tico
		 */
		for(int i=0; i < nuevaGeneracion.length; i++) {
			crearEntidadHija(nuevaGeneracion, i);
		}
		entidades = nuevaGeneracion; //Se sustituyen las entidades actuales por las nuevas
		numGeneraciones++; //Se incrementa el contador de generaciones
	}

	/**
	 * Genera un nuevo objeto Entidad apartir del cruce de los genes de dos parientes
	 * aleatorios del pool gen�tico y tras realizar una posible mutaci�n
	 * @param nuevaGeneracion la colecci�n de entidades que sustituir� a la anterior
	 * @param i el �ndice de la entidad que se va a crear
	 */
	private void crearEntidadHija(Entidad[] nuevaGeneracion, int i) {
		//El primer pariente se obtiene del primer indice aleatorio que escoge
		int indPariente1 = random.nextInt(poolGenetico.size());
		Entidad pariente1 = poolGenetico.get(indPariente1);
		//El segundo pariente deber� ser uno con una aptitud distinta al primero
		Entidad pariente2 = encontrarParienteDistinto(pariente1);
		//Se obtiene el ADN que tendr� el hijo tras cruzar el de ambos parientes
		ADN adnHijo = cruzarEntidades(pariente1, pariente2);
		mutar(adnHijo); //Se le aplican las mutaciones que surjan aleatoriamente
		/* Como creamos una nueva entidad a partir de un genotipo ya construido,
		 * le pasamos el ADN como argumento a su constructor, y ya no generar�
		 * genes aleatoriamente (excluyendo aquellos que han mutado)
		 */
		nuevaGeneracion[i] = new Entidad(this, adnHijo, i);
	}
	
	/**
	 * Busca un pariente aleatorio en el pool gen�tico cuya aptitud no sea igual a
	 * la del primer pariente escogido para reproducirse.
	 * Sirve para evitar que un pariente se reproduzca consigo mismo, ya que no tendr�a
	 * mucho sentido en este contexto, y para que no se cruce con alguno con la misma
	 * aptitud, puesto que eso disminuir�a considerablemente la variaci�n de genes en
	 * los hijos obtenidos para la siguiente generaci�n, evitando que evolucionen
	 * @param pariente1: primer pariente que se ha elegido para cruzarse
	 * @return el segundo pariente que se cruzar� con el primero
	 */
	private Entidad encontrarParienteDistinto(Entidad pariente1) {
		int indPariente2;
		/* Hasta que no salga un pariente con una aptitud distinta, contin�a sacando
		 * parientes aleatoriamente del pool gen�tico
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
	 * una "tirada al aire de una moneda" con cada gen que deber� tener
	 * @param pariente1
	 * @param pariente2
	 * @return el ADN (genotipo) con los genes obtenidos del cruce
	 */
	private ADN cruzarEntidades(Entidad pariente1, Entidad pariente2) {
		//Inicia un array de vectores cuyo tama�o son los frames de vida de la entidad
		PVector[] genesHijo = new PVector[tiempoVida];
		//Obtiene los genes de ambos parientes
		PVector[] genesPariente1 = pariente1.getAdn().getGenes();
	    PVector[] genesPariente2 = pariente2.getAdn().getGenes();
	    /* El n�mero de cruces que se realizar�n depender� de si el tiempo de vida de los padres
	     * es mayor o igual que el de la nueva generaci�n o no. En el primer caso ser� simplemente
	     * equivalente al tiempo de vida, pero en el caso de que sea menor, no habr� suficientes
	     * cruces para rellenar sus genes, as� que har� tantos cruces como la resta de la 
	     * diferencia entre estos haya, para evitar errores de elementos vac�os.
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
		 * genes aleatorios, ya que no quedan m�s cruces que hacer
		 */
		if(!parientesVivenIgualOMas) {
			rellenarGenesExtra(genesHijo, numCruces);
		}
		return new ADN(genesHijo);
	}

	/**
	 * Asigna a un gen del hijo que se est� creando el gen correspondiente al de alguno de
	 * los dos parientes seg�n el que toque aleatoriamente
	 * @param genPariente1 gen del primer pariente
	 * @param genPariente2 gen del segundo pariente
	 */
	private PVector elegirGenes(PVector genPariente1, PVector genPariente2) {
		/* En cada gen tendr� un 50% de posibilidades de elegir el de un pariente u otro.
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
	 * @param numCruces: los cruces que lleg� a hacer hasta que no quedaban m�s genes
	 */
	private void rellenarGenesExtra(PVector[] genesHijo, int numCruces) {
		//Genera un ADN temporal que sirve para obtener los genes aleatorios
		ADN adnTemp = new ADN(tiempoVida);
		//Comienza el bucle donde dej� el anterior para los genes que faltan
		for(int i=numCruces; i < tiempoVida; i++) {
			genesHijo[i] = adnTemp.getGenes()[i]; //Rellena con un gen aleatorio
		}
	}
	
	/**
	 * Recorre todos los genes del ADN de una entidad y por cada uno existir� 
	 * la posibilidad (determinada por la probabilidad de la tasa de mutaci�n)
	 * de que mute transform�ndose en un vector completamente aleatorio
	 * @param adnHijo que podr� ser alterado
	 */
	private void mutar(ADN adnHijo) {
		for(PVector gen : adnHijo.getGenes()) {
			if(random.nextDouble(1) < tasaMutacion) {
				adnHijo.generarGenAleatorio(gen);
			}
		}
	}
	
	/**
	 * Reduce el n�mero de decimales en notaci�n cient�fica que muestra la aptitud
	 * para hacerlo m�s legible en la interfaz
	 * @param aptitud
	 * @return la aptitud redondeada mostrando solo 4 decimales extra
	 */
	private String redondearAptitud(double aptitud) {
		/* Muestra solo un n�mero seguido de como mucho 3 decimales y la notaci�n
		 * cient�fica correspondiente seg�n los decimales que tenga la aptitud */
		DecimalFormat df = new DecimalFormat("0.0##E0");
		return df.format(aptitud);
	}
	
	/**
	 * Incrementa el n�mero de colisiones con obst�culos y lo muestra en el panel de control
	 */
	public void incrNumColisiones() {
		contexto.getControlador().getVisualizador().actualizarPanel("Colisiones", ++numColisiones);
	}

	/**
	 * Incrementa el n�mero de llegadas a la meta y lo muestra en el panel de control
	 */
	public void incrNumLlegadas() {
		contexto.getControlador().getVisualizador().actualizarPanel("Metas", ++numLlegadas);
	}
	
	/**
	 * Incrementa el n�mero de colisiones con obst�culos  de esta generacion y lo muestra en el panel de control
	 */
	public void incrNumColisionesActual() {
		contexto.getControlador().getVisualizador().actualizarPanel("ColisionesActual", ++numColisionesActual);
	}

	/**
	 * Incrementa el n�mero de llegadas a la meta de esta generacion y lo muestra en el panel de control
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
