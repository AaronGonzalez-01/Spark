package Obligatorio2;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BibliotecaService {

    private final Map<String, Libro> libros = new HashMap<>();


    private static final Pattern ISBN_PATTERN =
            Pattern.compile("^[0-9\\-]{10,17}$");

    public List<Libro> obtenerTodos(String autor) {
        Collection<Libro> valores = libros.values();
        if (autor == null || autor.isBlank()) {
            return new ArrayList<>(valores);
        }
        String autorBuscado = autor.toLowerCase();
        return valores.stream()
                .filter(l -> l.getAutor() != null &&
                        l.getAutor().toLowerCase().contains(autorBuscado))
                .collect(Collectors.toList());
    }

    public Libro obtenerPorIsbn(String isbn) {
        return libros.get(isbn);
    }

    public boolean existeIsbn(String isbn) {
        return libros.containsKey(isbn);
    }

    public Libro crear(Libro libro) {
        libro.setDisponible(true);
        libros.put(libro.getIsbn(), libro);
        return libro;
    }

    public Libro actualizar(String isbn, Libro datos) {
        Libro existente = libros.get(isbn);
        if (existente == null) {
            return null;
        }

        if (datos.getTitulo() != null) {
            existente.setTitulo(datos.getTitulo());
        }
        if (datos.getAutor() != null) {
            existente.setAutor(datos.getAutor());
        }
        if (datos.getAñoPublicacion() != 0) {
            existente.setAñoPublicacion(datos.getAñoPublicacion());
        }
        existente.setDisponible(datos.isDisponible());
        return existente;
    }

    public Libro eliminar(String isbn) {
        return libros.remove(isbn);
    }

    public List<Libro> buscarPorTitulo(String q) {
        String query = q.toLowerCase();
        return libros.values().stream()
                .filter(l -> l.getTitulo() != null &&
                        l.getTitulo().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    public boolean esIsbnValido(String isbn) {
        if (isbn == null) return false;
        String trimmed = isbn.trim();
        if (!ISBN_PATTERN.matcher(trimmed).matches()) {
            return false;
        }
        String soloDigitos = trimmed.replace("-", "");
        return soloDigitos.length() == 10 || soloDigitos.length() == 13;
    }
}

