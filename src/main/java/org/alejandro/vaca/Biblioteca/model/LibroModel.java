package org.alejandro.vaca.Biblioteca.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LibroModel(
        String id,
        @NotBlank(message = "El Titulo No Puede Estar Vacio") @Size(max = 60, message = "El Titulo No Puede Superar Los 60 Caracteres") @Pattern(regexp = "^[\\p{L} \\-.&@,+]+$", message = "El Titulo No Puede Llevar Caracteres No Validos") String titulo,
        @NotBlank(message = "El Autor No Puede Estar Vacio") @Size(max = 60, message = "El Autor No Puede Superar Los 60 Caracteres") @Pattern(regexp = "^[\\p{L} \\-.&,]+$", message = "El Nombre Del Autor(es) No Puede Llevar Caracteres No Validos") String autor,
        @Min(value = 1, message = "El año de la edicion no sera menor a 1") @Max(value = 20, message = "El año de la edicion no puede ser mayor al 20") int edicion,
        @NotBlank(message = "El campo editorial no puede estar vacio") @Pattern(regexp = "^[\\p{L} .&]+$", message = "La editorial solo acepta caracteres alfabéticos, incluyendo espacios, puntos y el símbolo &.") String editorial,
        @Min(value = 1899, message = "El Año No Puede Ser Menor A 1990") @Max(value = 2027, message = "El Año No Puede Ser Mayor A 2026") int anio) {
    public LibroModel {
        titulo = titulo == null ? null : titulo.trim();
        autor = autor == null ? null : autor.trim();
        editorial = editorial == null ? null : editorial.trim();
    }
}
