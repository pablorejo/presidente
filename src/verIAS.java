import java.util.ArrayList;

import inteligencia.Entrenamiento;
import jugador.CartasEnJuego;
import jugador.Jugador;

public class verIAS {
    public static void main(String[] args) {
        Entrenamiento entreno = new Entrenamiento(new ArrayList<Jugador>(), new CartasEnJuego());

        boolean entrenadas = false;
        int numeroDeJuegos = 1;

        if (args.length > 0) {
            for(int k = 0; k<args.length; k++){
                if (args[k].equals("-e")){
                    entrenadas = true;
                }else{
                    if (args[k] != null){
                        try {
                            numeroDeJuegos = Integer.parseInt(args[k]);
                        } catch (Exception e) {
                            System.out.println("El argumento " + k + " no es un número.");
                        }
                    }
                }
            }
        }

        if (entrenadas) {
            System.out.println("Jugando las últimas IAs entrenadas");
            entreno.cargarIas();
        }else{
            System.out.println("Jugando las ias registradas");
            entreno.cargarIasRegistro();
        }

        while (numeroDeJuegos > 0) {
            entreno.jugar();
            numeroDeJuegos--;
        }

        entreno.verInformacionIAs();
    }
}
