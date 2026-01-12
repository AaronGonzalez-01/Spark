package Obligatorio2;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) {
        port(4567);

        BibliotecaService servicio = new BibliotecaService();
        BibliotecaController controlador = new BibliotecaController(servicio);

        controlador.configurarRutas();
    }
}

