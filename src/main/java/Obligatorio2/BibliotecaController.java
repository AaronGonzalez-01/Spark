package Obligatorio2;

import com.google.gson.JsonSyntaxException;
import spark.Request;
import spark.Response;

import java.util.Collections;
import java.util.List;

public class BibliotecaController {

    private final BibliotecaService servicio;

    public BibliotecaController(BibliotecaService servicio) {
        this.servicio = servicio;
    }

    /* Registra todas las rutas de la API */
    public void configurarRutas() {
        spark.Spark.before((req, res) -> {
            res.type("application/json");
        });

        spark.Spark.get("/libros", this::obtenerLibros, JsonUtil.json());
        spark.Spark.get("/libros/:isbn", this::obtenerLibroPorIsbn, JsonUtil.json());
        spark.Spark.get("/libros/buscar", this::buscarLibros, JsonUtil.json());
        spark.Spark.post("/libros", this::crearLibro, JsonUtil.json());
        spark.Spark.put("/libros/:isbn", this::actualizarLibro, JsonUtil.json());
        spark.Spark.delete("/libros/:isbn", this::eliminarLibro, JsonUtil.json());

        spark.Spark.notFound((req, res) -> {
            res.type("application/json");
            res.status(404);
            return JsonUtil.toJson(Collections.singletonMap("error", "Recurso no encontrado"));
        });

        spark.Spark.internalServerError((req, res) -> {
            res.type("application/json");
            return JsonUtil.toJson(Collections.singletonMap("error", "Error interno del servidor"));
        });
    }

    /* GET /libros ?autor=X */
    public Object obtenerLibros(Request req, Response res) {
        String autor = req.queryParams("autor");
        List<Libro> resultado = servicio.obtenerTodos(autor);
        res.status(200);
        return resultado;
    }

    /* GET /libros/:isbn */
    public Object obtenerLibroPorIsbn(Request req, Response res) {
        String isbn = req.params("isbn");
        Libro libro = servicio.obtenerPorIsbn(isbn);
        if (libro == null) {
            res.status(404);
            return Collections.singletonMap("error", "Libro no encontrado");
        }
        res.status(200);
        return libro;
    }

    /* POST /libros */
    public Object crearLibro(Request req, Response res) {
        try {
            Libro libro = JsonUtil.fromJson(req.body(), Libro.class);

            if (libro == null ||
                    libro.getIsbn() == null ||
                    libro.getTitulo() == null ||
                    libro.getAutor() == null) {
                res.status(400);
                return Collections.singletonMap("error", "Datos incompletos");
            }

            String isbn = libro.getIsbn().trim();

            if (!servicio.esIsbnValido(isbn)) {
                res.status(400);
                return Collections.singletonMap("error", "ISBN inválido");
            }

            if (servicio.existeIsbn(isbn)) {
                res.status(409);
                return Collections.singletonMap("error", "Ya existe un libro con ese ISBN");
            }

            libro.setIsbn(isbn);
            Libro creado = servicio.crear(libro);
            res.status(201);
            return creado;

        } catch (JsonSyntaxException e) {
            res.status(400);
            return Collections.singletonMap("error", "JSON inválido");
        }
    }

    /* GET /libros/buscar?q=X */
    public Object buscarLibros(Request req, Response res) {
        String q = req.queryParams("q");
        if (q == null || q.isBlank()) {
            res.status(400);
            return Collections.singletonMap("error", "Parámetro de búsqueda 'q' requerido");
        }
        List<Libro> resultado = servicio.buscarPorTitulo(q);
        res.status(200);
        return resultado;
    }

    /* PUT /libros/:isbn */
    public Object actualizarLibro(Request req, Response res) {
        String isbn = req.params("isbn");
        Libro existente = servicio.obtenerPorIsbn(isbn);

        if (existente == null) {
            res.status(404);
            return Collections.singletonMap("error", "Libro no encontrado");
        }

        try {
            Libro datos = JsonUtil.fromJson(req.body(), Libro.class);
            Libro actualizado = servicio.actualizar(isbn, datos);
            res.status(200);
            return actualizado;
        } catch (JsonSyntaxException e) {
            res.status(400);
            return Collections.singletonMap("error", "JSON inválido");
        }
    }

    /* DELETE /libros/:isbn */
    public Object eliminarLibro(Request req, Response res) {
        String isbn = req.params("isbn");
        Libro eliminado = servicio.eliminar(isbn);

        if (eliminado == null) {
            res.status(404);
            return Collections.singletonMap("error", "Libro no encontrado");
        }

        res.status(204);
        return "";
    }
}

