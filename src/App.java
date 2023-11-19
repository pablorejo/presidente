import java.util.*;

public class App {
    static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) throws Exception {
        
        ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
        Baraja baraja = new Baraja();
        baraja.crearBaraja();
        baraja.mezclar();


        for (int k = 0; k < 4; k++){
            Mano mano = baraja.repartir();
            Jugador jugador = new Jugador(mano,Jugador.Role.Nada);
            jugador.setNombre("Jugador " + k);
            jugador.numero = k;
            jugadores.add(jugador);
        }

        int turno = 0;
        while (true) {
            CartasEnJuego cartasEnJuego = new CartasEnJuego();
            int nJugadoresPasan = 0;

            for (Jugador jugador: jugadores){
                jugador.pasa = false;
            }

            while (true) {
                // jugador0 nosotros
                int tamaño = 0;

               
                if (cartasEnJuego.ultiMano() != null && cartasEnJuego.ultiMano().cartas != null){
                    tamaño = cartasEnJuego.ultiMano().cartas.size();
                }

                switch (turno) {
                    case 0:
                        menuJugador(cartasEnJuego,nJugadoresPasan,jugadores.get(0));
                        
                        turno = 1;
                        break;
                        
                    default:
                        jugarJugador(cartasEnJuego,jugadores.get(turno),tamaño,nJugadoresPasan);
                        if (turno + 1 >= 4){
                            turno = 0;
                        }else{
                            turno ++;
                        }
                        break;
                }


                // Para saber si todos han pasado ya
                nJugadoresPasan = 0;
                for (Jugador jugador: jugadores){
                    if (jugador.pasa){
                        nJugadoresPasan++;
                    }
                }
                if (nJugadoresPasan >= 3){
                    turno = cartasEnJuego.ultiMano().jugador.numero;
                    break;
                }
            }
        }   
    }

    private static void menuJugador(CartasEnJuego cartasEnJuego,int nJugadoresPasan,Jugador jugador){
        boolean malLanzado = true;
        while (malLanzado && !jugador.pasa) {
            jugador.verMano();
            
            System.out.println("¿Que carta o cartas quiere lanzar? [ejem: 1,2]");
            System.out.println("O pasar [ejem: pass]");
            cartasEnJuego.verUltimaMano();
            
            String cartas = scanner.nextLine();
            if (cartas.equals("pass")){
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
                int tamaño_maximo = 0;
                if (!cartasEnJuego.manos.isEmpty()){
                    valor_max = cartasEnJuego.ultiMano().cartas.get(0).getValor();
                    tamaño_maximo = cartasEnJuego.ultiMano().cartas.size();
                }

                for (Carta carta: cartas_lanzadas){
                    if (carta.getValor() != valor_actual && (cartas_lanzadas.size() != tamaño_maximo && carta.getValor() != 13)){
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

    public static void jugarJugador(CartasEnJuego cartasEnJuego, Jugador jugador, int tamaño, int nJugadoresPasan){

        int valor = 0;
        if (cartasEnJuego.ultiMano() != null && cartasEnJuego.ultiMano().cartas.size()>0){
            valor = cartasEnJuego.ultiMano().cartas.get(0).getValor();
        }
    
        Mano mano = jugador.echarCarta(tamaño,valor);

        if (mano.cartas.size() == 0){
            System.out.println(jugador.nombre + " Pasa");
            jugador.pasa = true;
            nJugadoresPasan++;
        }else{
            System.out.println(jugador.nombre + " ha echado");
            mano.verMano();
            cartasEnJuego.manos.add(mano);
            jugador.mano.cartas.removeAll(mano.cartas);
        }
    }
}


class Baraja {
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

class Carta {
    private int numero;
    private int valor;
    private String palo;
   
    public Carta(int numero, String palo){
        this.numero = numero;
        this.palo = palo;
        this.setValor();
    }   

    public void setValor(){
        if (this.numero == 1){
            this.valor = 11;
        }else if (this.numero == 2){
            if (this.palo.equals("Oros")){
                this.valor = 13;
            }else{
                this.valor = 12;
            }
        }else{
            this.valor = numero;
        }
    }

    public int getValor(){
        return this.valor;
    }
    public String getCarta(){
        return (String.valueOf(numero) + " " + palo);
    }
}

class Jugador{
    Mano mano = new Mano(new ArrayList<Carta>(),this);
    boolean pasa = false;
    int puntos = 0;
    String nombre = "";
    Role miRole;
    int numero = 0;

    public Jugador(Mano mano,Role role){
        this.mano = mano;
        mano.jugador = this;
        this.miRole = role;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }



    public void verMano(){
        this.mano.ordenarMano();
        this.mano.verMano();
    }

    public Carta getCarta(int carta){
        this.mano.ordenarMano();
        return this.mano.cartas.get(carta);
    }

    /*Esta función echa las cartas mas altas que tenga */
    public Mano echarCarta(int tamaño, int valor){
        mano.ordenarManoAscendente();
        ArrayList<Carta> cartas_lanzar = new ArrayList<Carta>();
        
        int valor_actual = 0;
        for (Carta carta: mano.cartas) {
            if (carta.getValor() > valor && (valor_actual == 0 || carta.getValor() == valor_actual)){
                valor_actual = carta.getValor();
                cartas_lanzar.add(carta);
            }else if (carta.getValor() != valor_actual){
                cartas_lanzar = new ArrayList<Carta>();
            }

            if (cartas_lanzar.size() == tamaño || tamaño == 0){
                break;
            }
        }
        if (cartas_lanzar.size() != tamaño && tamaño != 0){
            cartas_lanzar = new ArrayList<Carta>();
        }

        //Eliminamos las cartas que vamos a lanzar de nuestra mano
        this.mano.cartas.removeAll(cartas_lanzar);
        Mano nuevaMano = new Mano(cartas_lanzar,this);
        this.mano.ordenarMano();
        return nuevaMano;
    }

    public enum  Role {
        Presidente("Presidente"),
        VicePresidente("VicePresidente"),
        ViceComemierda("ViceComemierda"),
        Comemierda("Comemierda"),
        Nada("Nada");
        private final String stringValue;

        Role(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }
    }
}

class Mano{
    ArrayList<Carta> cartas;
    Jugador jugador;

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


class CartasEnJuego{
    ArrayList<Mano> manos = new ArrayList<Mano>();
    
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