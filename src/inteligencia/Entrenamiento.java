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
    private static double probabilidadMutacion = 0.13;// probabilidad de que una parte de la matriz mute de manera aleatoria.
    private static int numeroTotalDeIAs = 100; // Numero total de ias en cada generación.
    private static int numeroDeIasRecuperables = 12; //La siguiente generacion solo tendrá descendencia de las n mejores.
    private static int conservacionDeNIAs = 5; //La siguiente generacion conservara intactas las n mejores.
    private static int numeroDeEntrenamientos = 1000;// Numero de generaciones.
    private static Double desgaste = 0.95; // Las peores ias tendran menos probabilidad de fusionarse con respecto a este desgaste.
    private static Double fusion = 0.999; // Probabilidad de que una ia se fusione con otra.
    private static String carpetaGuardarRedesNeuronales = "redesNeuronales/"; // Carpeta donde se guardaran las ias.
    private static int cadaNguardamos = 3; // Cada n generaciones se guardaran las ias por seguridad.

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

    public void cargarIas(){
        System.out.println("Cargargando IAs");
        for(int k = 0; k < numeroTotalDeIAs; k++){
            IA ia = new IA(null, k, jugadores, cartasEnJuego);
            ia.recuperarRedNeuronal(carpetaGuardarRedesNeuronales + "IA_" + k + "_redNeuronal.dat");
            actualizarBarraDeCarga(k,numeroTotalDeIAs-1);
            ias.add(ia);
        }
    }

    public void entrenar(){
        int n = 0;
        while (n < numeroDeEntrenamientos) {
            System.out.println("Empezando ronda " + n);
            this.jugar();
            System.out.println("Recombinando Ias");

            this.obtenerIAs();
            n++;
            if (n%cadaNguardamos == 0 && n != numeroDeEntrenamientos){
                int j = 0;
                int numeroDeIAs = ias.size();
                System.out.println("Guardando redes");
                for (IA ia : ias) {
                    ia.guardarRedNeuronal(carpetaGuardarRedesNeuronales +  "IA_" + j + "_redNeuronal.dat");
                    j++;
                    actualizarBarraDeCarga(j,numeroDeIAs);

                }
                System.out.println("");
            }

        }
        int k = 0;
        System.out.println("Guardando redes");
        int numeroDeIAs = ias.size();
        for (IA ia : ias) {
            ia.guardarRedNeuronal(carpetaGuardarRedesNeuronales +  "IA_" + k + "_redNeuronal.dat");
            k++;
            actualizarBarraDeCarga(k,numeroDeIAs);
        }
    }

    

    private void jugar(){
        for(int k = 0; k < numeroTotalDeIAs-3; k++){
            
            for (int i = k + 1 ;i < numeroTotalDeIAs-3; i ++){

                ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
                Jugador jugador = new Jugador(new Mano(new ArrayList<Carta>(), null), Jugador.Role.Nada);
                jugador.setIA(this.ias.get(k));
                this.ias.get(k).jugador = jugador;
                jugadores.add(jugador);

                int siguiente = i;
                for (int j = 1; j <= 3; j++){
                    siguiente += 1;

                    Jugador jugador2 = new Jugador(new Mano(new ArrayList<Carta>(), null), Jugador.Role.Nada);
                    jugador2.setIA(this.ias.get(siguiente));
                    this.ias.get(siguiente).jugador = jugador;
                     jugadores.add(jugador2);

                }
                this.jugadores = jugadores;
                new Juego(jugadores);

            }
            
            
            actualizarBarraDeCarga(k,numeroTotalDeIAs-4);
        }
    }
    /**
     * Esta función crea la recombinacion de las ias a partir de las que mejor han quedado.
     */
    public void obtenerIAs(){
        Collections.sort(ias, new Comparator<IA>() {
            @Override
            public int compare(IA IA1, IA IA2) {
                // Ordenar en orden descendente
                return Integer.compare(IA2.getPuntacion(), IA1.getPuntacion());
            }
        });
        ArrayList<IA> ias2 = new ArrayList<IA>();
        
        Random random = new Random();
        
        for(int i = 0; i < conservacionDeNIAs; i++){
            ias.get(i).restartPuntos();
            ias2.add(ias.get(i));
        }

        int numeroDeIasCreadas = 0;
        while (numeroDeIasCreadas < numeroTotalDeIAs-conservacionDeNIAs) {
            for (int k = 0; k < numeroDeIasRecuperables; k ++) {
                Double probabilidadFusion = fusion * Math.pow(desgaste, k) ;
                for (int i = k + 1; i < numeroDeIasRecuperables; i ++) {
                    if (probabilidadFusion > random.nextDouble()){
                        IA ia = combinarIas(ias.get(k), ias.get(i));
                        ias2.add(ia);
                        numeroDeIasCreadas ++;
                    }
    
                    // Desgaste de la probabilidad
                    probabilidadFusion = probabilidadFusion * 0.9;
                    if (numeroDeIasCreadas == numeroTotalDeIAs-conservacionDeNIAs){
                        break;
                    }
                }
                if (numeroDeIasCreadas == numeroTotalDeIAs-conservacionDeNIAs){
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
        IA nuevaIa = new IA(null, 0, jugadores, cartasEnJuego);

        for (int k = 0; k < ia1.miRed.rDeNeuronas.size(); k++) {
            nuevaIa.miRed.rDeNeuronas.get(k).bias = crossover(ia1.miRed.rDeNeuronas.get(k).bias, ia2.miRed.rDeNeuronas.get(k).bias);
            nuevaIa.miRed.rDeNeuronas.get(k).w_matrix = crossover(ia1.miRed.rDeNeuronas.get(k).w_matrix, ia2.miRed.rDeNeuronas.get(k).w_matrix);
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
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (random.nextDouble() < probabilidadMutacion){
                    descendencia[i][j] = random.nextGaussian();
                }else{
                    if (random.nextDouble() > 0.5){
                        descendencia[i][j] = padre1[i][j];
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
