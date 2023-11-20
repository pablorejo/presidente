package jugador;
import java.util.*;
public class  CartasEnJuego{
    public ArrayList<Mano> manos = new ArrayList<Mano>();
    
    public Mano ultiMano(){
        if (manos.size() == 0){
            return null;
        }
        Mano mano = new Mano(new ArrayList<Carta>(), null);

        for (int k = manos.size() -1; k >= 0; k--){
            mano = manos.get(k);
            if (mano.cartas.size() > 0 && !mano.jugador.pasa){
                return mano;
            }
        }
        return mano;
    }

    public void verUltimaMano(){
        Mano mano = ultiMano();
        if (mano != null){
            if (mano.cartas.size() > 0){
                System.out.println("Ultima mano");
                mano.verMano();
            }else{
                System.out.println("Tablero vacio");
            }
        }else{
            System.out.println("Tablero vacio");
        }
    }

    public void ponerPuntuaciones(){
        int nJugadores = 4;

        for (int k = manos.size() -1; k == 0; k--){
            if (manos.get(k).cartas.size() > 0){
                Jugador jugador = manos.get(k).jugador;

                switch (nJugadores) {
                    case 4:
                    jugador.puntos += 3;
                    jugador.miRole = Jugador.Role.Presidente;
                    nJugadores--;
                    break;
                    case 3:
                    jugador.puntos += 3;
                    jugador.miRole = Jugador.Role.VicePresidente;
                    
                    nJugadores--;
                    break;
                    case 2:
                    jugador.puntos += 3;
                    jugador.miRole = Jugador.Role.ViceComemierda;
                    nJugadores--;
                        break;
                    case 1:
                    jugador.puntos += 3;
                    jugador.miRole = Jugador.Role.Comemierda;
                    nJugadores--;
                    break;
                    
                    default:
                    break;
                }
                if (nJugadores == 0){
                    break;
                }
            }

        }

        nJugadores = 4;
        for (int k = manos.size() -1; k == 0; k--){
            if (manos.get(k).cartas.size() == 0){
                Jugador jugador = manos.get(k).jugador;

                switch (nJugadores) {
                    case 4:
                        jugador.puntos += 3;
                        jugador.miRole = Jugador.Role.Presidente;
                        nJugadores--;
                        break;
                    case 3:
                        jugador.puntos += 3;
                        jugador.miRole = Jugador.Role.VicePresidente;
                        
                        nJugadores--;
                        break;
                    case 2:
                        jugador.puntos += 3;
                        jugador.miRole = Jugador.Role.ViceComemierda;
                        nJugadores--;
                        break;
                    case 1:
                        jugador.puntos += 3;
                        jugador.miRole = Jugador.Role.Comemierda;
                        nJugadores--;
                        break;
                    
                    default:
                        break;
                }
                if (nJugadores == 0){
                    break;
                }
            }
        }
    }

}
