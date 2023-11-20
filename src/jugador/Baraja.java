package jugador;
import java.util.*;
public class  Baraja {
    ArrayList<Carta> cartas = new ArrayList<Carta>();
    
    public void crearBaraja(){
        String[] palos = {"Oros", "Bastos", "Copas", "Espadas"};
        for (String palo : palos){
            for (int i = 1; i <= 10; i++){
                Carta carta = new Carta(i, palo);
                cartas.add(carta);
            }
        }
    }

    public void mezclar(){
        Collections.shuffle(this.cartas);
    }

    public Mano repartir(){
        ArrayList<Carta> cartasMano = new ArrayList<Carta>();
        for (int k = 0; k < 10; k++){
            Carta carta = this.cartas.get(k);
            cartasMano.add(carta);
        }
        for (Carta carta: cartasMano){
            this.cartas.remove(carta);
        }
        Mano mano = new Mano(cartasMano,null);
        return mano;
    }
}


