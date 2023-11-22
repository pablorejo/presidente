package inteligencia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class RedNeuronal implements Serializable{
    private static String ficheroEnDisco = "red_neuronal.dat";
    private FuncionDeActivacion singma = new FuncionDeActivacion(FuncionDeActivacion.TipoFuncion.Sigmoide);
    private ArrayList<CapaDeNeuronas> rDeNeuronas = new ArrayList<CapaDeNeuronas>();

    /**
     * Crea una red neuronal.
     * @param arrayNNeuronas array list que contiene el numero de neuronas que tendrá cada capa
     */
    public RedNeuronal(ArrayList<Integer> arrayNNeuronas){

        for (int i = 0; i < arrayNNeuronas.size()-1 ; i++){
            CapaDeNeuronas capa = new CapaDeNeuronas(arrayNNeuronas.get(i),arrayNNeuronas.get(i+1), singma);
            rDeNeuronas.add(capa);
        }

    }

    /**
     * Esta funcion va a entrenar la red neuronal
     * @param Yp parametros de entrada, tiene que ser una matiz de 1Xn 
     * @param Yr parametros de salida
     * @param learning_rate tasa de aprendizaje
     */
    public void train(Double[][] Yp, Double[][] Yr, Double learning_rate){

        /***** Forward pass (paso hacia adelante)  */
        Double[][] Yp_prima = new Double[Yp[0].length][Yp[1].length];
        Yp_prima = Yp;
        
        for (CapaDeNeuronas capa: rDeNeuronas){
            // Suma ponderada Z = (Yp*capa.w_matrix) + capa.w_matrix
            Double[][] Z = sumarMatrices(multiplicarMatrices(Yp_prima, capa.w_matrix),capa.w_matrix);
            Double[][] a = capa.resultadoFuncionDeActivacion(Z);
            Yp_prima = new Double[a[0].length][a[1].length];
            Yp_prima = a;
        }
    }  
    
    public Double[][] predict(Double[][] Yp, Double[][] Yr){

        /***** Forward pass (paso hacia adelante)  */
        Double[][] Yp_prima = new Double[Yp[0].length][Yp[1].length];
        Yp_prima = Yp;

        
        for (CapaDeNeuronas capa: rDeNeuronas){
            // Suma ponderada Z = (Yp*capa.w_matrix) + capa.w_matrix
            Double[][] Z = sumarMatrices(multiplicarMatrices(Yp_prima, capa.w_matrix),capa.w_matrix);
            Double[][] a = capa.resultadoFuncionDeActivacion(Z);
            Yp_prima = new Double[a[0].length][a[1].length];
            Yp_prima = a;
        }

        return Yp_prima;
    }

    private static Double[][] multiplicarMatrices(Double[][] a, Double[][] b) {
        // Se comprueba si las matrices se pueden multiplicar
        if (a[0].length != b.length) {
            throw new IllegalArgumentException("Las matrices no son compatibles para la multiplicación");
        }

        Double[][] c = new Double[a.length][b[0].length];
        // se comprueba si las matrices se pueden multiplicar
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < a[0].length; k++) {
                    // aquí se multiplica la matriz
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }

    public Double[][] sumarMatrices(Double[][] matriz1, Double[][] matriz2){
        if (matriz1 == null || matriz2 == null) {
            throw new IllegalArgumentException("Las matrices no deben ser nulas");
        }

        int filas = matriz1.length;
        int columnas = matriz1[0].length;

        // Es necesario que tengan las mismas filas y columnas
        if (filas != matriz2.length || columnas != matriz2[0].length) {
            throw new IllegalArgumentException("Las dimensiones de las matrices no son compatibles para la suma");
        }

        // Primero hacer la suma
        Double[][] matrizSuma = new Double[matriz1.length][matriz1[0].length];
        for (int y = 0; y < matriz1.length; y++) {
            for (int x = 0; x < matriz1[y].length; x++) {
                Double suma = matriz1[y][x] + matriz2[y][x];
                matrizSuma[y][x] = suma;
            }
        }
        return matrizSuma;
    }

    public Double errorCuadraticoMedio(Double[] Yp, Double[] Yr){
        Double sum = 0.0;

        // Sumamos todos los errores cuadráticos
        for (int i = 0; i < Yp.length; i++) {
            sum += Math.pow(Yp[i] - Yr[i], 2);
        }

        // devolvemos la media dividiendo entre la longitud
        return sum / Yp.length;
    };

    public Double errorCuadraticoMedioDerivada(Double[] Yp, Double[] Yr){
        Double sum = 0.0;

        // Sumamos todos los errores cuadráticos
        for (int i = 0; i < Yp.length; i++) {
            sum += Yp.length - Yr.length;
        }

        // devolvemos la media dividiendo entre la longitud
        return sum ;
    };

    public void guardarEnDisco(){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ficheroEnDisco))) {
            oos.writeObject(this);
            System.out.println("Objeto guardado correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RedNeuronal recuperarObjetoDesdeDisco() {
        RedNeuronal redNeuronalRecuperada = null;

        // Recuperar el objeto desde el archivo
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ficheroEnDisco))) {
            redNeuronalRecuperada = (RedNeuronal) ois.readObject();
            System.out.println("Objeto recuperado correctamente.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return redNeuronalRecuperada;
    }



    private class CapaDeNeuronas {
        private int numeroConexiones;
        private int numeroNeuronas;
        private FuncionDeActivacion funcionDeActivacion;
        private Double[] bias; 
        private Double[][] w_matrix; 
        
        public CapaDeNeuronas(int numeroConexiones, int numeroNeuronas, FuncionDeActivacion funcionDeActivacion){
            this.numeroConexiones = numeroConexiones;
            this.numeroNeuronas = numeroNeuronas;
            this.funcionDeActivacion = funcionDeActivacion;
            
            Random random = new Random();
            
            bias = new Double[numeroNeuronas];
            for (int j = 0; j < numeroNeuronas; j++) {
                bias[j] = random.nextDouble()*2 - 1; //Multiplicamos por dos y restamos 1 para que estea entre -1, 1
            }
            
            
            w_matrix = new Double[numeroConexiones][numeroNeuronas];
            for (int i = 0; i < numeroConexiones; i++) {
                for (int j = 0; j < numeroNeuronas; j++) {
                    w_matrix[i][j] = random.nextDouble();
                }
            }
        }

        public Double[][] resultadoFuncionDeActivacion(Double[][] x){
            Double[][] resultado = new Double[x[0].length][x[1].length];
            for (int i = 0; i < x[0].length; i ++){
                for (int k = 0; k < x[1].length; k ++){
                    resultado[i][k] +=  this.funcionDeActivacion.resultado(x[i][k]);
                }
            }
            return resultado;
        }
    }

    private class FuncionDeActivacion {
        private TipoFuncion tipoFuncion;
        public FuncionDeActivacion(TipoFuncion tipoFuncion){
            this.tipoFuncion = tipoFuncion;

        }

        public Double resultado(Double x){
            Double resultado = 0.0;
            switch (tipoFuncion) {
                case Sigmoide:
                    resultado = 1.0 / (1.0 + Math.exp(-x));
                    break;
                case ReLu:
                    if (x > 0){
                        resultado = x;
                    }else{
                        resultado = 0.0;
                    }
                    break;
            
                default:
                    break;
            }

            return resultado;
        }

        public Double derivada(Double x){
            Double resultado = 0.0;
            switch (tipoFuncion) {
                case Sigmoide:
                    resultado = x * (1-x);
                    break;
                case ReLu:
                    if (x > 0){
                        resultado = 1.0;
                    }else{
                        resultado = 0.0;
                    }
                    break;
            
                default:
                    break;
            }

            return resultado;

        }
        public enum  TipoFuncion {
            Sigmoide("Sigmoide"),
            ReLu("ReLu");
            private final String stringValue;
    
            TipoFuncion(String stringValue) {
                this.stringValue = stringValue;
            }
    
            public String getStringValue() {
                return stringValue;
            }
        }

        
    }
}


