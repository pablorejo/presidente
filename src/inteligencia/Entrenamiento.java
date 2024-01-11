package inteligencia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import inteligencia.RedNeuronal.CapaDeNeuronas;
import jugador.Carta;
import jugador.CartasEnJuego;
import jugador.Juego;
import jugador.Jugador;
import jugador.Mano;

public class Entrenamiento {
    ArrayList<IA> ias = new ArrayList<IA>();
    ArrayList<Jugador> jugadores;
    CartasEnJuego cartasEnJuego;
    private static double probabilidadMutacion = 0.05;// probabilidad de que una parte de la matriz mute de manera aleatoria.
    private static int numeroTotalDeIAs = 150; // Numero total de ias en cada generación.

    private static int numeroDeEntrenamientos = 100000;// Numero de generaciones.
    private static Double desgaste = 0.95; // Las peores ias tendran menos probabilidad de fusionarse con respecto a este desgaste.
    private static Double fusion = 0.999; // Probabilidad de que una ia se fusione con otra.
    private static String carpetaGuardarRedesNeuronales = "redesNeuronales/"; // Carpeta donde se guardaran las ias.
    private static String carpetaGuardarRedesNeuronalesRegistros = "redesNeuronales/registros/"; // Carpeta donde se guardaran las ias cada 10 generaciones
    private static String carpetaGuardarRedesNeuronalesMejores = "redesNeuronales/mejores/"; // Carpeta donde se guardan las 3 mejores redes
    private static int cadaNguardamos = 4; // Cada n generaciones se guardaran las ias por seguridad.
    private int NUMERO_JUGADAS_POR_RONDA = 20;
    private int UltimoID;

    // Esta variable está para guardar las mejores ias de cada jugada
    private IA _primeraMejorIA;
    private IA _segundaMejorIA;
    private IA _terceraMejorIA;
    
    public Entrenamiento(ArrayList<Jugador> jugadores,CartasEnJuego cartasEnJuego){
        this.jugadores = jugadores;
        this.cartasEnJuego = cartasEnJuego;
    }

    public void crearIas(){
        for(int k = 0; k < numeroTotalDeIAs; k++){
            IA ia = new IA(null, k, jugadores, cartasEnJuego);
            ias.add(ia);
        }
    }

    public void parsearAFichero(){
        this._primeraMejorIA.miRed.guardarRedString(carpetaGuardarRedesNeuronalesMejores + "red1.txt");
        this._segundaMejorIA.miRed.guardarRedString(carpetaGuardarRedesNeuronalesMejores + "red2.txt");
        this._terceraMejorIA.miRed.guardarRedString(carpetaGuardarRedesNeuronalesMejores + "red3.txt");
    }

    public void recuperarDesdeFichero(){
        this._primeraMejorIA.miRed.recuperarRedString(carpetaGuardarRedesNeuronalesMejores + "red1.txt");
        this._segundaMejorIA.miRed.recuperarRedString(carpetaGuardarRedesNeuronalesMejores + "red2.txt");
        this._terceraMejorIA.miRed.recuperarRedString(carpetaGuardarRedesNeuronalesMejores + "red3.txt");
    }

    public void cargarIas(){
        int ultimoID = 0;
        System.out.println("Cargargando IAs");
        for(int k = 0; k < numeroTotalDeIAs; k++){

            IA ia = new IA(null, k, jugadores, cartasEnJuego);
            ia.recuperarRedNeuronal(carpetaGuardarRedesNeuronales + "IA_" + k + "_redNeuronal.dat");
            
            if (ia.miRed != null){
                if (ia.miRed.getID() > ultimoID){
                    ultimoID = ia.miRed.getID();
                }
                ias.add(ia);
            }

            actualizarBarraDeCarga(k,numeroTotalDeIAs-1);
        }
        
        int faltan = numeroTotalDeIAs - ias.size();

        for(int k = 0; k < faltan; k ++){
            ultimoID++;
            IA ia = new IA(null, ultimoID, jugadores, cartasEnJuego);
            ias.add(ia);
        }

        this.UltimoID = ultimoID;
    }

    public void cargarIasRegistro(){
        
        System.out.println("Cargargando IAs");
        int empezando = 0;
        int iterando = 200;
        for(int k = empezando; k < iterando; k++){
            IA ia = new IA(null, k, jugadores, cartasEnJuego);
            ia.recuperarRedNeuronal(carpetaGuardarRedesNeuronalesRegistros + "IA_generacion_" + k + "_RedNeuronal.dat");
            actualizarBarraDeCarga(k-empezando,iterando-1-empezando);
            if (ia.miRed != null){
                ias.add(ia);
            }
        }
    }

    public void verInformacionIAs(){
        this.ordenarIasDescendente();
        for (IA ia : ias) {
            System.out.println("La red neuronal con ID: " + ia.miRed.getID() + " de la generacion: " + ia.miRed.getGeneracion() + " ha obtenido una puntuacion de " + ia.getPuntacion());
        }
    }

    public void entrenar(){
        int n = 0;
        while (n < numeroDeEntrenamientos) {
            System.out.println("Empezando ronda " + n);
            this.jugar();
            System.out.println("Recombinando Ias");
            n++;
            if (n%cadaNguardamos == 0 && n != numeroDeEntrenamientos){
                guardarRedesNeuronales();
                
                System.out.println("");
            }
            this.obtenerIAs();
        }
        guardarRedesNeuronales();
    }


    private void ordenarIasDescendente(){
        Collections.sort(ias, new Comparator<IA>() {
            @Override
            public int compare(IA IA1, IA IA2) {
                // Ordenar en orden descendente
                return Integer.compare(IA2.getPuntacion(), IA1.getPuntacion());
            }
        });
    }
    
    private void guardarRedesNeuronales(){
        this.parsearAFichero();
        this.ordenarIasDescendente();
        
        int k = 0;
        System.out.println("Guardando redes");

        ias.get(0).guardarRedNeuronal(carpetaGuardarRedesNeuronalesRegistros + "IA_generacion_" + ias.get(0).miRed.getGeneracion() + "_RedNeuronal.dat");

        int numeroDeIAs = ias.size();
        for (IA ia : ias) {
                
            ia.guardarRedNeuronal(carpetaGuardarRedesNeuronales +  "IA_" + k + "_redNeuronal.dat");
            k++;
            actualizarBarraDeCarga(k,numeroDeIAs);
        }
    }
    
    public void jugar(){
        IA primeraMejorIA = this.ias.get(0);
        IA segundaMejorIA = this.ias.get(1);
        IA terceraMejorIA = this.ias.get(2);

        for(int k = 3; k < ias.size(); k++){

            ArrayList<Jugador> jugadores2 = new ArrayList<Jugador>();
            Jugador mejorJugador = new Jugador(new Mano(new ArrayList<Carta>(), null), Jugador.Role.Nada);
            mejorJugador.setIA(primeraMejorIA);
            primeraMejorIA.jugador = mejorJugador;
            jugadores2.add(mejorJugador);

            Jugador segundoJugador = new Jugador(new Mano(new ArrayList<Carta>(), null), Jugador.Role.Nada);
            segundoJugador.setIA(segundaMejorIA);
            segundaMejorIA.jugador = segundoJugador;
            jugadores2.add(segundoJugador);

            Jugador tercerJugador = new Jugador(new Mano(new ArrayList<Carta>(), null), Jugador.Role.Nada);
            tercerJugador.setIA(terceraMejorIA);
            terceraMejorIA.jugador = tercerJugador;
            jugadores2.add(tercerJugador);


            Jugador siguienteJugador = new Jugador(new Mano(new ArrayList<Carta>(), null), Jugador.Role.Nada);
            siguienteJugador.setIA(this.ias.get(k));
            this.ias.get(k).jugador = siguienteJugador;
            jugadores2.add(siguienteJugador);

            


            for (int j = 0; j < this.NUMERO_JUGADAS_POR_RONDA; j++){
                new Juego(jugadores2);
            }

            Collections.sort(jugadores2, new Comparator<Jugador>() {
                @Override
                public int compare(Jugador jugador1, Jugador jugador2) {
                    // Ordenar en orden descendente
                    return Integer.compare(jugador2.getPuntos(), jugador1.getPuntos());
                }
            });

            primeraMejorIA = jugadores2.get(0).getIa();
            segundaMejorIA = jugadores2.get(1).getIa();
            terceraMejorIA = jugadores2.get(2).getIa();
            
            actualizarBarraDeCarga(k,ias.size()-1);
        }

        this._primeraMejorIA = primeraMejorIA;
        System.out.println("La mejor red neuronal ha sido "+ primeraMejorIA.miRed.getID());
        this._segundaMejorIA = segundaMejorIA;
        this._terceraMejorIA = terceraMejorIA;
    }
    /**
     * Esta función crea la recombinacion de las ias a partir de las que mejor han quedado.
     */
    public void obtenerIAs(){
        this.ordenarIasDescendente();
        ArrayList<IA> ias2 = new ArrayList<IA>();
        
        Random random = new Random();
        
        _primeraMejorIA.restartPuntos();
        ias2.add(_primeraMejorIA);

        _segundaMejorIA.restartPuntos();
        ias2.add(_segundaMejorIA);

        _terceraMejorIA.restartPuntos();
        ias2.add(_terceraMejorIA);

        int numeroDeIasCreadas = 0;
        while (numeroDeIasCreadas < numeroTotalDeIAs-3) {
            for (int k = 0; k < 3; k ++) {
                Double probabilidadFusion = fusion * Math.pow(desgaste, k) ;
                for (int i = k + 1; i < 3; i ++) {
                    if (probabilidadFusion > random.nextDouble()){
                        IA ia = combinarIas(ias2.get(k), ias2.get(i));
                        ias2.add(ia);
                        numeroDeIasCreadas ++;
                    }
    
                    // Desgaste de la probabilidad
                    probabilidadFusion = probabilidadFusion * 0.9;
                    if (numeroDeIasCreadas == numeroTotalDeIAs-3){
                        break;
                    }
                }
                if (numeroDeIasCreadas == numeroTotalDeIAs-3){
                    break;
                }
            }
        }

        this.ias = new ArrayList<IA>();
        this.ias.addAll(ias2);
    }


    /**
     * Esta funcion devuelve una IA hija de dos ias
     * @param ia1 Objeto IA padre
     * @param ia2 Objeto IA madre
     * @return Objeto IA hijo
     */
    public IA combinarIas(IA ia1, IA ia2){
        this.UltimoID++;
        IA nuevaIa = new IA(null,this.UltimoID , jugadores, cartasEnJuego);

        for (int k = 0; k < ia1.miRed.rDeNeuronas.size(); k++) {

            // se obtiene el bias que pasara a la siguiente generacion
            nuevaIa.miRed.rDeNeuronas.get(k).bias = crossover(ia1.miRed.rDeNeuronas.get(k).bias, ia2.miRed.rDeNeuronas.get(k).bias);
            
            // se recombina las filas de cada W_matrix entre si
            nuevaIa.miRed.rDeNeuronas.get(k).w_matrix = crossover(ia1.miRed.rDeNeuronas.get(k).w_matrix, ia2.miRed.rDeNeuronas.get(k).w_matrix);
        }

        if (ia1.miRed.getGeneracion() > ia2.miRed.getGeneracion()){
            nuevaIa.miRed.setGeneracion(ia1.miRed.getGeneracion() + 1);
        }else{
            nuevaIa.miRed.setGeneracion(ia2.miRed.getGeneracion() + 1);
        }

        return nuevaIa;
    }

    /**
     * Esta funcion hace la combinacion de dos matrices con los genes del padre y de la madre con una determinada probabilidad de mutación
     * @param padre1 matriz padre1
     * @param padre2 matriz padre2
     * @return el hijo
     */
    public static Double[][] crossover(Double[][] padre1, Double[][] padre2) {
        Random random = new Random();
        int filas = padre1.length;
        int columnas = padre1[0].length;


        // Crear la matriz descendencia
        Double[][] descendencia = new Double[filas][columnas];

        // Combinar las partes de los padres antes y después del punto de cruce
        for (int j = 0; j < columnas; j++) {
            if (random.nextDouble() > 0.5){
                for (int i = 0; i < filas; i++) {
                    if (random.nextDouble() < probabilidadMutacion){
                        descendencia[i][j] = random.nextGaussian();
                    }else{
                        descendencia[i][j] = padre1[i][j];
                    }
                }
            }else{
                for (int i = 0; i < filas; i++) {
                    if (random.nextDouble() < probabilidadMutacion){
                        descendencia[i][j] = random.nextGaussian();
                    }else{
                        descendencia[i][j] = padre2[i][j];
                    }
                }
            }
        }

        return descendencia;
    }

    private static void actualizarBarraDeCarga(int progreso, int total) {
        int longitudBarra = 50; // Longitud total de la barra de carga

        // Calcula la cantidad de caracteres "#" para la barra de carga
        int caracteresCarga = (int) (((double) progreso / total) * longitudBarra);

        // Construye la barra de carga
        StringBuilder barraCarga = new StringBuilder("[");
        for (int i = 0; i < longitudBarra; i++) {
            if (i < caracteresCarga) {
                barraCarga.append("#");
            } else {
                barraCarga.append(" ");
            }
        }
        barraCarga.append("]");

        // Imprime la barra de carga en la misma línea
        System.out.print("\r" + barraCarga + " " + progreso * 100 / total + "%");
        System.out.flush();

        // Espera un breve tiempo para que la barra de carga sea visible
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Si el progreso es completo, imprime una nueva línea
        if (progreso == total) {
            System.out.println();
        }
    }
}
