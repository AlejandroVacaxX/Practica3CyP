package org.alejandro.vaca.Biblioteca.controller;

import jakarta.validation.Valid;
import org.alejandro.vaca.Biblioteca.model.LibroModel;
import org.alejandro.vaca.Biblioteca.service.LibroService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biblioteca/libros")
public class LibroController {
    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public List<LibroModel> obtenerLibro() {
        return libroService.listarLibros();
    }

    @GetMapping("/id/{id}")
    public LibroModel obtenerLibroPorId(@PathVariable String id) {
        return libroService.buscarLibroPorId(id);
    }

    /*
     * obtenerLibroPorTitulo(String titulo),
     * obtenerLibroPorAutor(string autor).
     */
    @GetMapping("/titulo/{titulo}")
    public List<LibroModel> obtenerLibrosPorTitulos(@PathVariable String titulo) {
        return libroService.obtenerLibroPorTitulo(titulo);

    }

    @GetMapping("/autor/{autor}")
    public List<LibroModel> obtenerLibroPorAutor(@PathVariable String autor) {
        return libroService.obtenerLibrosPorAutor(autor);
    }

    @PostMapping
    public LibroModel guardarLibro(@Valid @RequestBody LibroModel libroModel) {
        return libroService.registrarLibro(libroModel);
    }

}
