package org.alejandro.vaca.Biblioteca.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.alejandro.vaca.Biblioteca.model.LibroModel;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@Repository
public class LibroRepositorio {

    private static final String COLLECTION = "libros";

    private final Firestore firestore;

    public LibroRepositorio(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<LibroModel> obtenerTodos() {

        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION).get();
            QuerySnapshot querySnapshot = future.get();
            List<LibroModel> libros = new ArrayList<>();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                LibroModel libro = document.toObject(LibroModel.class);

                if (libro != null) {
                    libros.add(new LibroModel(
                            document.getId(),
                            libro.titulo(),
                            libro.autor(),
                            libro.edicion(),
                            libro.editorial(),
                            libro.anio()

                    ));
                } else {
                    System.out.println("No Se Encontro Ningun El Libro");
                }
            }
            return libros;
        } catch (Exception e) {
            throw new RuntimeException("No Fue Posible Obtener Los Libros Por El Error" + e);
        }
    }

    public Optional<LibroModel> obtenerPorId(String id) {
        if (id == null || id.isBlank())
            return Optional.empty();
        try {
            // esta madre lo busca con el ID que firebase le crea
            DocumentReference documentReference = firestore.collection(COLLECTION).document(id);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                LibroModel libro = document.toObject(LibroModel.class);

                if (libro != null) {
                    return Optional.of(new LibroModel(
                            document.getId(),
                            libro.titulo(),
                            libro.autor(),
                            libro.edicion(),
                            libro.editorial(),
                            libro.anio()

                    ));
                } else {
                    System.out.println("No Se Encontro Ningun El Libro Con El Id " + id);
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("No Fue Posible Obtener El Libro Con El Id " + id + " Por El Error " + e);
        }
    }

    public List<LibroModel> obtenerLibrosPorTitulos(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            return new ArrayList<>();
        }

        try {
            Query query = firestore.collection(COLLECTION).whereEqualTo("titulo", titulo);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            List<LibroModel> librosEncontrados = new ArrayList<>();

            for (DocumentSnapshot document : documents) {
                LibroModel libro = document.toObject(LibroModel.class);

                if (libro != null) {
                    librosEncontrados.add(new LibroModel(
                            document.getId(),
                            libro.titulo(),
                            libro.autor(),
                            libro.edicion(),
                            libro.editorial(),
                            libro.anio()));
                }
            }

            if (librosEncontrados.isEmpty()) {
                System.out.println("No se encontró ningún libro con el titulo: " + titulo);
            }

            // Devolvemos la lista
            return librosEncontrados;

        } catch (Exception e) {
            throw new RuntimeException(
                    "No fue posible obtener los libros con el titulo: " + titulo + " por el error: " + e.getMessage());
        }
    }

    public List<LibroModel> obtenerLibrosPorAutor(String autor) {

        if (autor == null || autor.isBlank()) {
            return new ArrayList<>();
        }

        try {
            Query query = firestore.collection(COLLECTION).whereEqualTo("autor", autor);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            // creamos una lista para guardar los resultados, porque ahora no queremos la
            // primer coincidencia, queremos todas
            List<LibroModel> librosEncontrados = new ArrayList<>();
            // lo recorremos con un for
            for (DocumentSnapshot document : documents) {
                LibroModel libro = document.toObject(LibroModel.class);

                if (libro != null) {
                    librosEncontrados.add(new LibroModel(
                            document.getId(),
                            libro.titulo(),
                            libro.autor(),
                            libro.edicion(),
                            libro.editorial(),
                            libro.anio()));
                }
            }

            if (librosEncontrados.isEmpty()) {
                System.out.println("No se encontró ningún libro del autor: " + autor);
            }

            // Devolvemos la lista
            return librosEncontrados;

        } catch (Exception e) {
            throw new RuntimeException(
                    "No fue posible obtener los libros del autor: " + autor + " por el error: " + e.getMessage());
        }
    }

    public LibroModel guardar(LibroModel libro) {
        try {
            CollectionReference collectionReference = firestore.collection(COLLECTION);
            DocumentReference documentReference = collectionReference.document();
            ApiFuture<WriteResult> future = documentReference.set(Map.of(
                    "titulo", libro.titulo(),
                    "autor", libro.autor(),
                    "edicion", libro.edicion(),
                    "editorial", libro.editorial(),
                    "anio", libro.anio()));

            future.get();

            return new LibroModel(
                    documentReference.getId(),
                    libro.titulo(),
                    libro.autor(),
                    libro.edicion(),
                    libro.editorial(),
                    libro.anio()

            );

        } catch (Exception e) {
            throw new RuntimeException("No Fue Posible Guardar El Libro Por El Error" + e);
        }
    }
}
