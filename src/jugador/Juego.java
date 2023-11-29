package jugador;
import java.util.*;
import inteligencia.*;
import jugador.*;

public class Juego {
    static Scanner scanner = new Scanner(System.in);
    static int RONDAS_TOTALES;
    ArrayList<Jugador> jugadores = new ArrayList<Jugador>();

    public Juego(ArrayList<Jugador> jugadores){
        this.jugadores = jugadores;

        for (int k = 0; k < 4; k++){
            jugadores.get(k).setNombre("Jugador " + k);
            jugadores.get(k).setRole(Jugador.Role.Nada);
            jugadores.get(k).numero = k;
            jugadores.get(k).setVerboseFalse();
        }

        Baraja baraja = new Baraja();
        RONDAS_TOTALES = 4;
        int rondas = RONDAS_TOTALES;

        while (rondas > 0) {

            baraja.crearBaraja();
            baraja.mezclar();
            
            for (Jugador jugador: jugadores){
                Mano mano = baraja.repartir();
                jugador.mano = mano;
            }

            int turno = 0;
            boolean fin_partida = false;
            ArrayList<Integer> ordenJugadores = new ArrayList<Integer>();

            
            if (rondas != RONDAS_TOTALES){
                for (Jugador jugador: jugadores){
                    if (jugador.getRole() == Jugador.Role.Comemierda){
                        break;
                    }
                }
                Mano comemierda = new Mano(new ArrayList<Carta>(), null);
                Mano viceComemierda = new Mano(new ArrayList<Carta>(), null);
                Mano vicePresidente = new Mano(new ArrayList<Carta>(), null);
                Mano presidente = new Mano(new ArrayList<Carta>(), null);

                //#region Obtener cartas de los jugadores
                for (Jugador jugador: jugadores){
                    if (jugador.getRole() == Jugador.Role.Comemierda){
                        comemierda = jugador.getNCartas(2, true);
                        
                    }else if (jugador.getRole() == Jugador.Role.ViceComemierda){
                        viceComemierda = jugador.getNCartas(1, true);
                    }else{
                        if (jugador.getRole() == Jugador.Role.VicePresidente){
                            vicePresidente = jugador.getNCartas(1, false);
                        }else if (jugador.getRole() == Jugador.Role.Presidente){
                            presidente = jugador.getNCartas(2, false);
                        }
                    }
                }
                //#endregion Obtener cartas de los jugadores

                //#region Dar cartas a los jugadores
                for (Jugador jugador: jugadores){
                    if (jugador.getRole() == Jugador.Role.Comemierda){
                        jugador.mano.cartas.addAll(presidente.cartas);
                        turno = jugador.numero;
                    }
                    else if (jugador.getRole() == Jugador.Role.ViceComemierda){
                        jugador.mano.cartas.addAll(vicePresidente.cartas);
                    }
                    else if (jugador.getRole() == Jugador.Role.VicePresidente){
                        jugador.mano.cartas.addAll(viceComemierda.cartas);
                    }
                    else if (jugador.getRole() == Jugador.Role.Presidente){
                        jugador.mano.cartas.addAll(comemierda.cartas);
                    }
                }
                //#endregion Dar cartas a los jugadores

            }else if (rondas == RONDAS_TOTALES){
                for (Jugador jugador : jugadores) {
                    boolean fin = false;
                    for (Carta carta : jugador.mano.cartas){
                        if (carta.getValor() == 3 && carta.getPalo().equals("Oros")){
                            turno = jugador.numero;
                            fin = true;
                            break;
                        }
                    }
                    if (fin){
                        break;
                    }
                }
            }

            while (!fin_partida) {
                CartasEnJuego cartasEnJuego = new CartasEnJuego();
                
                int nJugadoresPasan = 0;
                
                for (Jugador jugador: jugadores){
                    jugador.noPasa();
                    jugador.setCartasEnJuego(cartasEnJuego);
                }
            
                while (true) {
                    // jugador0 nosotros
                    int tamano = 0;
                
                    if (cartasEnJuego.ultiMano() != null && cartasEnJuego.ultiMano().cartas != null){
                        tamano = cartasEnJuego.ultiMano().cartas.size();
                    }

                    int turno_anterior = turno;
                    if (!jugadores.get(turno).getFinalPartida()){
                        switch (turno) {
                            default:
                                jugarJugador(cartasEnJuego,jugadores.get(turno),tamano,nJugadoresPasan);
                                break;
                        }
                        
                        if (jugadores.get(turno_anterior).getFinalPartida()){
                            ordenJugadores.add(turno_anterior);
                        }
                    }

                    if (turno + 1 >= 4){
                        turno = 0;
                    }else{
                        turno ++;
                    }

                    if (ordenJugadores.size() == 3){
                        // Fin partida
                        fin_partida = true;

                        int k = 1;
                        for (Integer integer : ordenJugadores) {
                            Jugador jugador2 = jugadores.get(integer);
                            switch (k) {
                                case 1:
                                    jugador2.setRole(Jugador.Role.Presidente);
                                    break;
                                case 2:
                                    jugador2.setRole(Jugador.Role.VicePresidente);
                                    break;
                                case 3:
                                    jugador2.setRole(Jugador.Role.ViceComemierda);
                                    break;
                                default:
                                    jugador2.setRole(Jugador.Role.Nada);
                                    break;
                            }
                            k++;
                        }

                        // Para establecer al comemierda;
                        ArrayList<Integer> faltantes = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
                        faltantes.removeAll(ordenJugadores);
                        int indexComemierda = faltantes.get(0);
                        jugadores.get(indexComemierda).setRole(Jugador.Role.Comemierda);

                        // for (Jugador jugador : jugadores) {
                        //     jugador.verResultadosPartida();
                        // }
                        break;
                    }
                        
                    // Para saber si todos han pasado ya
                    nJugadoresPasan = 0;
                    for (Jugador jugador: jugadores){
                        if (jugador.getPasa() || jugador.getFinalPartida()){
                            nJugadoresPasan++;
                        }
                    }
                    if (nJugadoresPasan >= 3){
                        Mano uMano = cartasEnJuego.ultiMano();
                        if (!uMano.jugador.getPasa()){
                            turno = uMano.jugador.numero;
                            break;
                        }
                    }
                }
            }
            rondas--;
        }  
    }
   
    public static void jugarJugador(CartasEnJuego cartasEnJuego, Jugador jugador, int tamano, int nJugadoresPasan){

        int valor = 0;
        if (cartasEnJuego.ultiMano() != null && cartasEnJuego.ultiMano().cartas.size()>0){
            valor = cartasEnJuego.ultiMano().cartas.get(0).getValor();
        }
        Mano mano = jugador.echarCarta(tamano,valor);
        if (mano.cartas.size() == 0){
            jugador.siPasa();
            cartasEnJuego.manos.add(new Mano(new ArrayList<Carta>(), jugador));
            nJugadoresPasan++;
        }else{
            // mano.verMano();
            cartasEnJuego.manos.add(mano);
            jugador.mano.cartas.removeAll(mano.cartas);
        }
    }
}


