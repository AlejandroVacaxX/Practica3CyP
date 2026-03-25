package org.alejandro.vaca.Biblioteca;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.alejandro.vaca.Biblioteca.model.LibroModel;
import org.alejandro.vaca.Biblioteca.service.LibroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Scanner;
import java.util.Set;

@SpringBootApplication
public class Biblioteca {
    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext contexto = SpringApplication.run(Biblioteca.class, args);
        System.out.println("El Service Se Cerrara en 10 Segundos");
        Thread.sleep(10000);
        System.out.println("Cerrando El Servicio");
        contexto.close();
    }

    @Bean
    @Profile("!test")
    @ConditionalOnProperty(name = "app.cargar-datos", havingValue = "true", matchIfMissing = true)
    CommandLineRunner init(LibroService libroService, Validator validator) {
        return args -> {
            System.out.println("Aplicacion Biblioteca Inicializada Correctamente");

            Scanner scanner = new Scanner(System.in);
            LibroModel libroValido = null;

            while (libroValido == null) {
                try {
                    System.out.println("Ingresa El Nombre Del Libro:");
                    String titulo = scanner.nextLine();
                    System.out.println("Ingresa El Nombre Del Autor(es) Del Libro:");
                    String autor = scanner.nextLine();
                    scanner = new Scanner(System.in);

                    System.out.println("Ingresa la edicion del libro:");
                    String edicionText = scanner.nextLine();
                    int edicion = Integer.parseInt(edicionText);
                    System.out.println("Ingresa la editorial del libro:");
                    String editorial = scanner.nextLine();
                    System.out.println("Ingresa El Anio De Publicacion Del Libro");
                    String anioTexto = scanner.nextLine();
                    int anio = Integer.parseInt(anioTexto);

                    LibroModel libroTemporal = new LibroModel(null, titulo, autor, edicion, editorial, anio);
                    Set<ConstraintViolation<LibroModel>> constraintViolations = validator.validate(libroTemporal);
                    if (!constraintViolations.isEmpty()) {
                        System.out.println("Se Encontraron Errores En Los Datos Ingresados:");
                        for (ConstraintViolation<LibroModel> constraintViolation : constraintViolations) {
                            System.out.println(" - " + constraintViolation.getPropertyPath() + ":"
                                    + constraintViolation.getMessage());
                        }
                        System.out.println("Vuelve A Intentarlo\n");
                    } else {
                        libroValido = libroTemporal;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("El Anio Debe Ser Un Valor Numerico");
                } catch (Exception e) {
                    System.out.println("No Se Pudo Registrar El Libro Por El Error " + e + ". Vuelva A Intentarlo");
                }
            }
            try {
                libroService.registrarLibro(libroValido);
                System.out.println("Libro Registrado Correctamente");
            } catch (Exception e) {
                System.out.println("No Se Pudo Registrar El Libro Por El Error" + e + ". Vuelva A Intentarlo");
            }

            System.out.println("Libros Registrados En La Biblioteca");
            libroService.listarLibros().forEach(System.out::println);
            // System.out.println(libroService.buscarLibroPorId("8ZIvdfUnRNYWvoep5nvA"));
            System.out.println("Endpoint Disponible En:");
            System.out.println("http://localhost:8080/biblioteca/libros");
        };

    }
}
