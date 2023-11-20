package jugador;
import java.util.*;
public class  Mano{
    public ArrayList<Carta> cartas;
    public Jugador jugador;

    public Mano(ArrayList<Carta> cartas, Jugador jugador){
        this.cartas = cartas;
        this.ordenarMano();
        this.jugador = jugador;
    }

    
    public void verMano(){
        int i = 0;
        if (this.cartas.size() ==  0){
            System.out.println("No hay cartas");
        }else{
            for (Carta carta: this.cartas){
                System.out.println(i +") " + carta.getCarta());
                i++;
            }
        }
        System.out.println("");
    }

    public void ordenarMano(){
        Collections.sort(cartas, new Comparator<Carta>() {
            @Override
            public int compare(Carta carta1, Carta carta2) {
                // Ordenar por edad en orden descendente
                return Integer.compare(carta2.getValor(), carta1.getValor());
            }
        });
    }

    public void ordenarManoAscendente(){
        Collections.sort(cartas, new Comparator<Carta>() {
            @Override
            public int compare(Carta carta1, Carta carta2) {
                // Ordenar por edad en orden descendente
                return Integer.compare(carta1.getValor(), carta2.getValor());
            }
        });
    }
}
