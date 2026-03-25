package org.alejandro.vaca.Biblioteca.service;

import java.util.List;

import org.alejandro.vaca.Biblioteca.model.LibroModel;
import org.alejandro.vaca.Biblioteca.repository.LibroRepositorio;
import org.springframework.stereotype.Service;

@Service
public class LibroService {
    private final LibroRepositorio libroRepositorio;

    public LibroService(LibroRepositorio libroRepositorio) {
        this.libroRepositorio = libroRepositorio;
    }

    public List<LibroModel> listarLibros() {
        return libroRepositorio.obtenerTodos();
    }

    public LibroModel buscarLibroPorId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El Id No Es Valido");
        }
        return libroRepositorio.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("El Id" + id + " No Existe"));
    }

    public List<LibroModel> obtenerLibroPorTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El Titulo No Es Valido");
        }
        List<LibroModel> libros = libroRepositorio.obtenerLibrosPorTitulos(titulo);
        if (libros.isEmpty()) {
            throw new IllegalArgumentException("El titulo " + titulo + " no tiene libros registrados");
        }
        return libros;

    }

    public List<LibroModel> obtenerLibrosPorAutor(String autor) {
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El Autor No Es Valido");
        }
        List<LibroModel> libros = libroRepositorio.obtenerLibrosPorAutor(autor);
        if (libros.isEmpty()) {
            throw new IllegalArgumentException("El autor " + autor + " no tiene libros registrados");
        }
        return libros;

    }

    public LibroModel registrarLibro(LibroModel libroModel) {
        if (libroModel == null) {
            throw new IllegalArgumentException(
                    "El Campo Libro No Puede Ser Nulo");
        }

        boolean tituloAutorDuplicado = libroRepositorio.obtenerTodos().stream()
                .anyMatch(libro -> libro.titulo().equalsIgnoreCase(libroModel.titulo())
                        && libro.autor().equals(libroModel.autor()));
        if (tituloAutorDuplicado) {
            throw new IllegalStateException("Ya Existe Un Libro Con Ese Titulo Y Autor");
        }

        return libroRepositorio.guardar(libroModel);
    }

}
