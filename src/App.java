import java.util.*;
import inteligencia.*;
import jugador.*;

public class App {
    static Scanner scanner = new Scanner(System.in);
    static int RONDAS_TOTALES;
    public static void main(String[] args) throws Exception {
        
        ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
        
        for (int k = 0; k < 4; k++){
            Jugador jugador = new Jugador(new Mano(new ArrayList<Carta>(), null),Jugador.Role.Nada);
            jugador.setNombre("Jugador " + k);
            jugador.miRole = Jugador.Role.Nada;
            jugador.numero = k;
            jugadores.add(jugador);
        }
        
        Baraja baraja = new Baraja();


        // Defenir rondas totales
        while (true) {
            try {
                
                System.out.println("Cuantas rondas quieres jugar?");
                String rondaString = scanner.nextLine();
                RONDAS_TOTALES = Integer.parseInt(rondaString);
                break;
                
            } catch (Exception e) {
                System.out.println("Tienes que poner un número");
            }
        }
        
        int rondas = RONDAS_TOTALES;

        while (rondas > 0) {

            baraja.crearBaraja();
            baraja.mezclar();

            System.out.println("Desea continuar?");
            String seguir = scanner.nextLine();
            if (!seguir.equalsIgnoreCase("s") && !seguir.equalsIgnoreCase("si")) {
                System.exit(0);
             }
           
            for (Jugador jugador: jugadores){
                Mano mano = baraja.repartir();
                jugador.mano = mano;
            }

            int turno = 0;
            boolean fin_partida = false;
            ArrayList<Integer> ordenJugadores = new ArrayList<Integer>();

            
            if (rondas != RONDAS_TOTALES){
                for (Jugador jugador: jugadores){
                    if (jugador.miRole == Jugador.Role.Comemierda){
                        break;
                    }
                }
                Mano comemierda = new Mano(new ArrayList<Carta>(), null);
                Mano viceComemierda = new Mano(new ArrayList<Carta>(), null);
                Mano vicePresidente = new Mano(new ArrayList<Carta>(), null);
                Mano presidente = new Mano(new ArrayList<Carta>(), null);

                //#region Obtener cartas de los jugadores
                for (Jugador jugador: jugadores){
                    if (jugador.miRole == Jugador.Role.Comemierda){
                        comemierda = jugador.getNCartas(2, true);
                        
                    }else if (jugador.miRole == Jugador.Role.ViceComemierda){
                        viceComemierda = jugador.getNCartas(1, true);
                    }else if (jugador.numero == 0){
                        int cartasADar = 0;
                        if (jugador.miRole == Jugador.Role.VicePresidente){
                            cartasADar = 1;
                        }else if (jugador.miRole == Jugador.Role.Presidente){
                            cartasADar = 2;
                        }
                        Mano manoADar;
                        while (true) {
                            try {
                                jugador.verMano();
                                System.out.println("Elige " + cartasADar + " cartas para dar a tu rival");

                                String cartas = scanner.nextLine();
                                String[] selecionadas = cartas.split(",");
                
                                ArrayList<Carta> cartas_lanzadas = new ArrayList<Carta>();
                                
                                
                                for (String seleccion : selecionadas){
                                    int numero_index = Integer.parseInt(seleccion);
                                    Carta carta= jugador.getCarta(numero_index);
                                    cartas_lanzadas.add(carta);
                                }
                                
                                jugador.mano.cartas.removeAll(cartas_lanzadas);
                                manoADar = new Mano(cartas_lanzadas,jugador);

                                if (manoADar.cartas.size() == cartasADar){
                                    break;
                                }else{
                                    System.out.println("No as dado las cartas necesarias.");
                                }
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }

                        switch (cartasADar) {
                            case 1:
                                vicePresidente = manoADar;
                                break;
                            case 2:
                                presidente = manoADar;
                            default:
                                break;
                        }
                    } else{
                        if (jugador.miRole == Jugador.Role.VicePresidente){
                            vicePresidente = jugador.getNCartas(1, false);
                        }else if (jugador.miRole == Jugador.Role.Presidente){
                            presidente = jugador.getNCartas(2, false);
                        }
                    }

                }
                //#endregion Obtener cartas de los jugadores

                //#region Dar cartas a los jugadores
                for (Jugador jugador: jugadores){
                    if (jugador.miRole == Jugador.Role.Comemierda){
                        jugador.mano.cartas.addAll(presidente.cartas);
                    }
                    else if (jugador.miRole == Jugador.Role.ViceComemierda){
                        jugador.mano.cartas.addAll(vicePresidente.cartas);
                    }
                    else if (jugador.miRole == Jugador.Role.VicePresidente){
                        jugador.mano.cartas.addAll(viceComemierda.cartas);
                    }
                    else if (jugador.miRole == Jugador.Role.Presidente){
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
                    jugador.pasa = false;
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
                            case 0:
                                Mano uMano = cartasEnJuego.ultiMano();
                                
                                if (uMano == null || uMano.cartas.get(0).getValor() != 13){
                                    menuJugador(cartasEnJuego,nJugadoresPasan,jugadores.get(0));
                                }else{
                                    System.out.println("Han echado el dos de oros no puedes seguir");
                                    jugadores.get(0).pasa = true;
                                }
                                break;
                            
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
                                    jugador2.miRole = Jugador.Role.Presidente;
                                    jugador2.puntos += 3;
                                    break;
                                case 2:
                                    jugador2.miRole = Jugador.Role.VicePresidente;
                                    jugador2.puntos += 1;
                                    break;
                                case 3:
                                    jugador2.miRole = Jugador.Role.ViceComemierda;
                                    jugador2.puntos += 0;
                                    break;
                                case 4:
                                    jugador2.miRole = Jugador.Role.Comemierda;
                                    jugador2.puntos -= 2;
                                    break;

                                default:
                                    break;
                            }
                            k++;
                        }

                        // Para establecer al comemierda;
                        ArrayList<Integer> faltantes = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
                        faltantes.removeAll(ordenJugadores);
                        int indexComemierda = faltantes.get(0);
                        jugadores.get(indexComemierda).miRole = Jugador.Role.Comemierda;
                        jugadores.get(indexComemierda).puntos -= 2;

                        System.out.println("Fin partida");
                        for (Jugador jugador : jugadores) {
                            jugador.verResultadosPartida();
                        }
                        break;
                    }
                        
                    // Para saber si todos han pasado ya
                    nJugadoresPasan = 0;
                    for (Jugador jugador: jugadores){
                        if (jugador.pasa || jugador.getFinalPartida()){
                            nJugadoresPasan++;
                        }
                    }
                    if (nJugadoresPasan >= 3){
                        turno = cartasEnJuego.ultiMano().jugador.numero;
                        break;
                    }
                }
            }
            rondas--;
        }   
    
        System.out.println("Fin partida");
        for (Jugador jugador : jugadores) {
            jugador.verResultadosPartida();
        }
    }

    private static void menuJugador(CartasEnJuego cartasEnJuego,int nJugadoresPasan,Jugador jugador){
        boolean malLanzado = true;
        while (malLanzado && !jugador.pasa) {
            jugador.verMano();
            
            System.out.println("Que carta o cartas quiere lanzar? [ejem: 1,2]");
            System.out.println("O pasar [ejem: pass]");
            cartasEnJuego.verUltimaMano();
            
            String cartas = scanner.nextLine();
            if (cartas.equalsIgnoreCase("pass")){
                jugador.pasa = true;
                nJugadoresPasan ++;
                cartasEnJuego.manos.add(new Mano(new ArrayList<Carta>(), jugador));
                break;
            }
            try {
                
                String[] selecionadas = cartas.split(",");
                
                ArrayList<Carta> cartas_lanzadas = new ArrayList<Carta>();
                
                for (String seleccion : selecionadas){
                    int numero_index = Integer.parseInt(seleccion);
                    Carta carta= jugador.getCarta(numero_index);
                    cartas_lanzadas.add(carta);
                }
                
                int valor_max = 0;
                int valor_actual = cartas_lanzadas.get(0).getValor();
                int tamano_maximo = 0;
                if (!cartasEnJuego.manos.isEmpty()){
                    valor_max = cartasEnJuego.ultiMano().cartas.get(0).getValor();
                    tamano_maximo = cartasEnJuego.ultiMano().cartas.size();
                }

                for (Carta carta: cartas_lanzadas){
                    if (carta.getValor() != valor_actual && (cartas_lanzadas.size() != tamano_maximo && carta.getValor() != 13)){
                        malLanzado = true;
                        break;
                    }else{
                        if (valor_actual <= valor_max){
                            malLanzado = true;
                            break;
                        }
                        malLanzado = false;
                    }
                }
                if (malLanzado){
                    System.out.println("No se pueden echar esas cartas");
                }else{
                    Mano mano = new Mano(cartas_lanzadas,jugador);
                    jugador.mano.cartas.removeAll(cartas_lanzadas);
                    System.out.println("Has echado");
                    mano.verMano();
                    cartasEnJuego.manos.add(mano);
                }
            } catch (Exception e) {
                System.out.println("Formato erroneo");
            }
        }
    }

    public static void jugarJugador(CartasEnJuego cartasEnJuego, Jugador jugador, int tamano, int nJugadoresPasan){

        int valor = 0;
        if (cartasEnJuego.ultiMano() != null && cartasEnJuego.ultiMano().cartas.size()>0){
            valor = cartasEnJuego.ultiMano().cartas.get(0).getValor();
        }
        Mano mano = jugador.echarCarta(tamano,valor);
        if (mano.cartas.size() == 0){
            System.out.println(jugador.getNombre() + " Pasa");
            jugador.pasa = true;
            nJugadoresPasan++;
        }else{
            System.out.println(jugador.getNombre() + " ha echado");
            mano.verMano();
            cartasEnJuego.manos.add(mano);
            jugador.mano.cartas.removeAll(mano.cartas);
        }
    }
}


