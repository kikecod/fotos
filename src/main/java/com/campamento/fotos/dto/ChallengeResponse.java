package com.campamento.fotos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos de un reto con su estado para el usuario actual")
public class ChallengeResponse {

    @Schema(description = "ID del reto", example = "1")
    private Long id;

    @Schema(description = "Título del reto", example = "Foto del amanecer")
    private String title;

    @Schema(description = "Descripción del reto", example = "Toma una foto del amanecer")
    private String description;

    @Schema(description = "Hora de inicio", example = "2026-02-10T06:00:00")
    private LocalDateTime startTime;

    @Schema(description = "Hora límite", example = "2026-02-10T18:00:00")
    private LocalDateTime limitTime;

    @Schema(description = "Día del campamento", example = "1")
    private Integer dayNumber;

    @Schema(description = "Estado del reto para el usuario: PENDING (gris), COMPLETED (verde), EXPIRED (rojo)", example = "PENDING")
    private String status; // PENDING, COMPLETED, EXPIRED

    @Schema(description = "Cantidad de fotos subidas a este reto", example = "5")
    private Integer submissionCount;
}
