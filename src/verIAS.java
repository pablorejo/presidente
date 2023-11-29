import java.util.ArrayList;

import inteligencia.Entrenamiento;
import jugador.CartasEnJuego;
import jugador.Jugador;

public class verIAS {
    public static void main(String[] args) {
        Entrenamiento entreno = new Entrenamiento(new ArrayList<Jugador>(), new CartasEnJuego());
        entreno.verInformacionIAs();


    }    
}
