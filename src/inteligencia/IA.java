package inteligencia;

import java.util.ArrayList;

import jugador.*;

public class IA {
    private Jugador jugador;

    public IA(Jugador jugador){
        this.jugador = jugador;

    }

    /**
     * Esta función echa las cartas mas bajas que tenga el jugador con la condicion de que supere un cierto tamano y valor
     * @param tamano numero de cartas de igual valor que se necesitan para jugar
     * @param valor las cartas tendran que tener mas de este valor para poder jugar
     * @return Un objeto Mano que contendrá las cartas que decidio echar.
     */
    public Mano echarCarta(int tamano, int valor){
        Mano mano = new Mano(new ArrayList<Carta>(), jugador);
        return mano;
    }

    private ArrayList<ArrayList<Carta>> cartasPuedeEchar(int tamano, int valor){
        ArrayList<ArrayList<Carta>> cartasPuedeEchar = new ArrayList<ArrayList<Carta>>();
        ArrayList<Carta> setCartas = new ArrayList<Carta>();

        int tamanoSetCartas = 0;

        if (tamano != 0){
            for (Carta carta: this.jugador.mano.cartas){
                if (carta.getValor() > valor && tamanoSetCartas < tamano){
                    tamanoSetCartas ++;
                    setCartas.add(carta);
                }else{
                    tamanoSetCartas = 0;
                    setCartas = new ArrayList<Carta>();
                }
                
                if (tamanoSetCartas == tamano ){
                    tamanoSetCartas = 0;
                    cartasPuedeEchar.add(setCartas);
                    setCartas = new ArrayList<Carta>();
                }
            }
        }
        return cartasPuedeEchar;
    }

}
