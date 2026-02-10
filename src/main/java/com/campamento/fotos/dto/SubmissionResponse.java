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
@Schema(description = "Datos de una foto subida")
public class SubmissionResponse {

    @Schema(description = "ID de la submission", example = "1")
    private Long id;

    @Schema(description = "URL de la imagen", example = "http://localhost:8080/uploads/abc123.jpg")
    private String imageUrl;

    @Schema(description = "Fecha y hora de subida", example = "2026-02-10T07:30:00")
    private LocalDateTime uploadedAt;

    @Schema(description = "Nombre del usuario que subió la foto", example = "juanperez")
    private String username;

    @Schema(description = "ID del reto", example = "1")
    private Long challengeId;

    @Schema(description = "Título del reto", example = "Foto del amanecer")
    private String challengeTitle;
}
