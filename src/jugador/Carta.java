package jugador;

public class  Carta {
    private int numero;
    private int valor;
    private String palo;
    private Double ponderacion;

    public Carta(int numero, String palo){
        this.numero = numero;
        this.palo = palo;
        this.setValor();
    } 
    
    public void setPonderacion(Double ponderacion){
        this.ponderacion = ponderacion;
    }

    public Double getPonderacion(){
        return this.ponderacion;
    }

    private String getNumero(){
        String retorno = "";
        switch (this.numero) {
            case 10:
                retorno = "Rey";
                break;
            case 9:
                retorno = "Caballo";
                break;
            case 8:
                retorno = "Sota";
                break;
            default:
                retorno = String.valueOf(numero);
                break;
        }
        return retorno;
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

    public String getPalo(){
        return this.palo;
    }

    public String getCarta(){
        return (this.getNumero() + " de " + palo);
    }
}
