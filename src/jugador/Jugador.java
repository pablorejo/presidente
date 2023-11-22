package jugador;
import java.util.*;
public class Jugador{
    public  Mano mano = new Mano(new ArrayList<Carta>(),this);
    public boolean pasa = false;
    private int puntos = 0;
    String nombre = "";
    private Role miRole;
    public int numero = 0;

    public Jugador(Mano mano,Role role){
        this.mano = mano;
        mano.jugador = this;
        this.miRole = role;
    }

    public void setRole(Role rol){
        switch (rol) {
            case Presidente:
                this.puntos += 3;
                break;
            case VicePresidente:
                this.puntos += 1;
                break;
            case ViceComemierda:
                this.puntos -= 1;
                break;
            case Comemierda:
                this.puntos -= 3;
                break;
            default:
                break;
        }
        this.miRole = rol;
    }

    public Role getRole(){
        return this.miRole;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getNombre(){
        return this.nombre;
    }

    public boolean getFinalPartida(){
        return this.mano.cartas.size() == 0;
    }


    public void verMano(){
        this.mano.ordenarMano();
        this.mano.verMano();
    }

    public Carta getCarta(int carta){
        this.mano.ordenarMano();
        return this.mano.cartas.get(carta);
    }

    /**
     * Esta función echa las cartas mas bajas que tenga el jugador con la condicion de que supere un cierto tamano y valor
     * @param tamano numero de cartas de igual valor que se necesitan para jugar
     * @param valor las cartas tendran que tener mas de este valor para poder jugar
     * @return Un objeto Mano que contendrá las cartas que decidio echar.
     */
    public Mano echarCarta(int tamano, int valor){
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

            if (cartas_lanzar.size() == tamano || tamano == 0){
                break;
            }
        }
        if (cartas_lanzar.size() != tamano && tamano != 0){
            cartas_lanzar = new ArrayList<Carta>();
        }


        //Eliminamos las cartas que vamos a lanzar de nuestra mano
        this.mano.cartas.removeAll(cartas_lanzar);
        
        Mano nuevaMano = new Mano(cartas_lanzar,this);
        this.mano.ordenarMano();

        if (this.mano.cartas.size() == 0){
            System.out.println(this.nombre + " ha terminado.");
        }
        return nuevaMano;
    }

    /**
     * Esta funcion devolverá n cartas en un objeto Mano mas buenas o mas malas del mazo del jugador.
     * @param n numero de cartas a obtener
     * @param buenas si son cartas buenas a true y si son malas a false
     */
    public Mano getNCartas(int n, boolean buenas){
        if (buenas){
            this.mano.ordenarMano();
        }else{
            this.mano.ordenarManoAscendente();
        }

        List<Carta> subLista = this.mano.cartas.subList(0, n);
        Mano cartasADar = new Mano(new ArrayList<Carta>(subLista), this);
        this.mano.cartas.removeAll(subLista);
        this.mano.ordenarMano();
        return cartasADar;
    }

    public void verResultadosPartida(){
        System.out.println(this.nombre + " ha conseguido " + this.puntos);
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