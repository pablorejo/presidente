
import java.util.ArrayList;

import inteligencia.*;
import jugador.CartasEnJuego;
import jugador.Jugador;

public class main {
    public static void main(String[] args) {
        Entrenamiento entreno = new Entrenamiento(new ArrayList<Jugador>(), new CartasEnJuego());

        // entreno.cargarIas();
        entreno.crearIas();
        entreno.entrenar();
    }
}
