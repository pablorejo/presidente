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
            if (mano.cartas.size() > 0 && !mano.jugador.getPasa()){
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

    public int[] getCartasEchadas(){
        int[] cartas = new int[38];
        Arrays.fill(cartas, 0);
        int k = 0;
        for (Mano mano : manos) {
            for (Carta carta: mano.cartas){
                cartas[k] = carta.getValor();
                k++;
                if (k == 38){
                    break;
                }
            }
            if (k == 38){
                break;
            }
        }
        return cartas;
    }
}
