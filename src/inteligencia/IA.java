package inteligencia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import jugador.*;

public class IA {
    public Jugador jugador;
    public RedNeuronal miRed;
    public ArrayList<Jugador> jugadores;
    public CartasEnJuego cartasEnJuego;
    private int puntos = 0;

    public IA(Jugador jugador, int ID, ArrayList<Jugador> jugadores, CartasEnJuego cartasEnJuego){
        this.jugadores = jugadores;
        this.jugador = jugador;
        this.miRed = new RedNeuronal(ID);
        this.cartasEnJuego = cartasEnJuego;
    }

    /**
     * Esta función echa las cartas mas bajas que tenga el jugador con la condicion de que supere un cierto tamano y valor
     * @param tamano numero de cartas de igual valor que se necesitan para jugar
     * @param valor las cartas tendran que tener mas de este valor para poder jugar
     * @return Un objeto Mano que contendrá las cartas que decidio echar.
     */
    public Mano echarCarta(){
        
        Double[][] vectorSalida = predict();
        Mano mano = seleccionarCarta(vectorSalida);
        this.jugador.mano.cartas.removeAll(mano.cartas);
        return mano;
    }

    public Mano descartarNCartas(int n){
        Double[][] vectorSalida = predict();
        Mano mano = obtenerLasNPeores(n,vectorSalida);
        return mano;
    }

    public void setPuntos(int puntos){
        this.puntos += puntos;
    }
    private Double[][] predict(){
        int[] cartasEnLaMesa = getCartas(cartasEnJuego.ultiMano(),4);

        int[] cartasDelJugador = getCartas(jugador.mano, 10);

        int[] cartasEchadas = cartasEnJuego.getCartasEchadas();

        int[] cartasOtrosJugadores = new int[3];

        int[] tamañoCartas = new int[1];
        try {
            tamañoCartas[0] = cartasEnJuego.ultiMano().cartas.size();
        } catch (Exception e) {
            tamañoCartas[0] = 0;
        }
        

        // Concatenar los arrays
        int[] vector = new int[cartasEnLaMesa.length + cartasDelJugador.length + cartasEchadas.length + cartasOtrosJugadores.length + tamañoCartas.length];
        //#region concatenar los vectores
        System.arraycopy(cartasEnLaMesa, 0, vector, 0, cartasEnLaMesa.length-1);
        System.arraycopy(cartasDelJugador, 0, vector, cartasEnLaMesa.length, cartasDelJugador.length-1);
        System.arraycopy(cartasEchadas, 0, vector, cartasEnLaMesa.length + cartasDelJugador.length, cartasEchadas.length-1);
        System.arraycopy(cartasOtrosJugadores, 0, vector, cartasEnLaMesa.length + cartasDelJugador.length + cartasEchadas.length, cartasOtrosJugadores.length-1);
        System.arraycopy(tamañoCartas, 0, vector, cartasEnLaMesa.length + cartasDelJugador.length + cartasEchadas.length + cartasOtrosJugadores.length, tamañoCartas.length-1);
        //#endregion concatenar los vectores

        Double[][] vectorEntrada = new Double[1][vector.length];
        for (int i = 0; i < vector.length; i++) {
            vectorEntrada[0][i] = (double) vector[i];
        }
        
        Double[][] vectorSalida = miRed.predict(vectorEntrada);
        return vectorSalida;
    }

    private Mano obtenerLasNPeores(int n,Double[][] cartas){
        int i = 0;
        ArrayList<Carta> cartasJugador = this.jugador.mano.cartas;
        for (Carta carta : cartasJugador) {
            carta.setPonderacion(cartas[0][i]);
            i++;
        }

        // Ordenamos las cartas primero la que mas ponderación tenga
        Collections.sort(cartasJugador, new Comparator<Carta>() {
            @Override
            public int compare(Carta carta1, Carta carta2) {
                // Ordenar por edad en orden descendente
                return Double.compare(carta1.getPonderacion(), carta2.getPonderacion());
            }
        });
        ArrayList<Carta> peoresCartas = new ArrayList<Carta>();
        for (int k = 0; k < n; k++){
            peoresCartas.add(cartasJugador.get(k));
        }

        this.jugador.mano.cartas.removeAll(peoresCartas);

        Mano mano = new Mano(peoresCartas, jugador);
        return mano;
    }
    /**
     * Esta funcion devuelve la mano a partir del array de salida de la red neuronal
     * @param cartas array de Double de la red neuronal
     * @return Objeto Mano que tiene que echar
     */
    private Mano seleccionarCarta(Double[][] cartas){
        int i = 0;
        ArrayList<Carta> cartasJugador = this.jugador.mano.cartas;
        
        for (Carta carta : cartasJugador) {
            
            carta.setPonderacion(cartas[0][i]);
            i++;
        }
        
        int tamano = 0;
        int valor = 0;
        boolean vacio = false;
        // Si hay cartas en la mesa el tamaño de las cartas que hay que echar esta definido
        Mano ultiMano = cartasEnJuego.ultiMano();
        if (cartasEnJuego.manos.size() > 0 && ultiMano.cartas.size() > 0){
            tamano = ultiMano.cartas.size();
            valor =  ultiMano.cartas.get(0).getValor();
            vacio = false;
        }else{
            tamano = (int) Math.round(cartas[0][10]) % 3 + 1;
            valor = 0;
            vacio = true;
        }

        ArrayList<Mano> manosPuedeEchar = new ArrayList<Mano>();
        for(int k = tamano; k >= 1; k--){

            manosPuedeEchar = cartasPuedeEchar(k, valor);
            
            // Ordenamos las cartas primero la que mas ponderación tenga
            Collections.sort(manosPuedeEchar, new Comparator<Mano>() {
                @Override
                public int compare(Mano mano1, Mano mano2) {
                    // Ordenar por edad en orden descendente
                    return Double.compare(mano2.getPonderacion(), mano1.getPonderacion());
                }
            });
            
            if(manosPuedeEchar.size() > 0){
                break;
            }

            // Si no esta vacio no sigue probando y sale.
            if (!vacio){
                break;
            }
        }

        if(manosPuedeEchar.size() > 0){
            this.jugador.mano.cartas.removeAll(manosPuedeEchar.get(0).cartas);
            return manosPuedeEchar.get(0);
        }else{
            return new Mano(new ArrayList<Carta>(), jugador);
        }
    }

    public int getPuntacion(){
        return this.puntos;
    }


    private int[] getCartas(Mano mano, int tamaño){
        int[] cartasInt = new int[tamaño];
        Arrays.fill(cartasInt, 0);
        int tamano_mano;
        if (mano != null){
            tamano_mano = mano.cartas.size();
        }else{
            tamano_mano = 0;
        }
        for (int k = 0; k < tamaño; k++) {
            if (k < tamano_mano){
                cartasInt[k] = mano.cartas.get(k).getValor();
            }else{
                cartasInt[k] = 0;
            }
        }
        return cartasInt;
    }

    private ArrayList<Mano> cartasPuedeEchar(int tamano, int valor){
        ArrayList<Mano> cartasPuedeEchar = new ArrayList<Mano>();
        ArrayList<Carta> setCartas = new ArrayList<Carta>();

        int tamanoSetCartas = 0;
        int ultimoValor = 0;

        if (tamano != 0){
            for (Carta carta: this.jugador.mano.cartas){
                if (ultimoValor == 0){
                    ultimoValor = carta.getValor();
                }
                if (carta.getValor() > valor && carta.getValor() == ultimoValor && tamanoSetCartas < tamano){
                    tamanoSetCartas ++;
                    ultimoValor = carta.getValor();
                    setCartas.add(carta);
                }else{
                    if (carta.getValor() > valor && tamanoSetCartas < tamano){
                        setCartas = new ArrayList<Carta>();
                        tamanoSetCartas = 1;
                        ultimoValor = carta.getValor();
                        setCartas.add(carta);
                    }else{
                        tamanoSetCartas = 0;
                        ultimoValor = 0;
                        setCartas = new ArrayList<Carta>();
                    }
                }
                
                if (tamanoSetCartas == tamano ){
                    tamanoSetCartas = 0;
                    Mano mano = new Mano(setCartas, this.jugador);
                    cartasPuedeEchar.add(mano);
                    setCartas = new ArrayList<Carta>();
                }
            }
        }
        return cartasPuedeEchar;
    }

    public void recuperarRedNeuronal(String ficheroEnDisco) {
        RedNeuronal redNeuronalRecuperada = null;

        // Recuperar el objeto desde el archivo
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ficheroEnDisco))) {
            redNeuronalRecuperada = (RedNeuronal) ois.readObject();
            System.out.println("Objeto recuperado correctamente.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.miRed = redNeuronalRecuperada;
    }

    public void guardarRedNeuronal(String ficheroEnDisco){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ficheroEnDisco))) {
            oos.writeObject(this.miRed);
            System.out.println("Objeto guardado correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
