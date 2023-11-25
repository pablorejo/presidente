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
    private static double probabilidadMutacion = 0.1;
    private static int numeroDeBuenos = 2;
    private static int numeroTotalDeIAs = 4;
    private static int numeroDeEntrenamientos = 2;

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
        for(int k = 0; k < numeroTotalDeIAs; k++){
            IA ia = new IA(null, k, jugadores, cartasEnJuego);
            ia.recuperarRedNeuronal("IA_" + k + "_redNeuronal.dat");
            ias.add(ia);
        }
    }

    public void entrenar(){
        int n = 0;
        while (n < numeroDeEntrenamientos) {
            this.jugar();
            this.obtenerIAs();
            n++;
        }
        int k = 0;
        for (IA ia : ias) {
            ia.miRed.guardarEnDisco("IA_" + k + "_redNeuronal.dat");
            k++;
        }
    }
    private void jugar(){
        for(int k = 0; k < numeroTotalDeIAs-3; k++){
            for(int i = 0; i < numeroTotalDeIAs-3; i++){
                ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
                Jugador jugador = new Jugador(new Mano(new ArrayList<Carta>(), null), Jugador.Role.Nada);
                jugador.setIA(this.ias.get(k));
                this.ias.get(k).jugador = jugador;
                jugadores.add(jugador);

                int siguiente = i;
                for (int j = 1; j <= 3; j++){
                    if (i == k){
                        siguiente = i+j;
                    }
                    Jugador jugador2 = new Jugador(new Mano(new ArrayList<Carta>(), null), Jugador.Role.Nada);
                    jugador2.setIA(this.ias.get(siguiente));
                    this.ias.get(siguiente).jugador = jugador;
                    jugadores.add(jugador2);
                }
                this.jugadores = jugadores;
                new Juego(jugadores);
            }
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
        Double desgaste = 0.9;

        int numeroDeIasCreadas = 0;
        while (numeroDeIasCreadas < numeroTotalDeIAs) {
            for (int k = 0; k < ias.size(); k ++) {
                Double probabilidadFusion = 0.99 * Math.pow(desgaste, k) ;
                for (int i = k + 1; i < ias.size(); i ++) {
                    if (probabilidadFusion > random.nextDouble()){
                        IA ia = combinarIas(ias.get(k), ias.get(i));
                        ias2.add(ia);
                        numeroDeIasCreadas ++;
    
                    }
    
                    // Desgaste de la probabilidad
                    probabilidadFusion = probabilidadFusion * 0.9;
                    if (numeroDeIasCreadas == numeroTotalDeIAs){
                        break;
                    }
                }
                if (numeroDeIasCreadas == numeroTotalDeIAs){
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
}
