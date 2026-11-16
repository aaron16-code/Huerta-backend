package cl.duoc.huertoescolar.huertoapi.dto;

import cl.duoc.huertoescolar.huertoapi.model.EstadoReporte;
import cl.duoc.huertoescolar.huertoapi.model.TipoProblema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProblemaDto(
        Integer id,
        @NotNull(message = "El sector es obligatorio") Integer sectorId,
        Integer cultivoId,
        @NotNull(message = "El tipo es obligatorio") TipoProblema tipo,
        @NotBlank(message = "Describe brevemente el problema") @Size(max = 1000) String descripcion,
        Long fechaReporte,
        @NotNull(message = "Falta quién reportó el problema") Integer reportadoPorId,
        String fotoUri,
        EstadoReporte estado,
        String comentarioDocente,
        Long fechaCierre
) {
}
