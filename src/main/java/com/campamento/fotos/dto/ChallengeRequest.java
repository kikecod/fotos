package com.campamento.fotos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos para crear un nuevo reto fotográfico")
public class ChallengeRequest {

    @NotBlank(message = "El título es obligatorio")
    @Schema(description = "Título del reto", example = "Foto del amanecer")
    private String title;

    @Schema(description = "Descripción del reto", example = "Toma una foto del amanecer desde el campamento")
    private String description;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Schema(description = "Hora de inicio del reto", example = "2026-02-10T06:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "La hora límite es obligatoria")
    @Schema(description = "Hora límite para subir fotos", example = "2026-02-10T18:00:00")
    private LocalDateTime limitTime;

    @NotNull(message = "El número de día es obligatorio")
    @Schema(description = "Día del campamento", example = "1")
    private Integer dayNumber;
}
