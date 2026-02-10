package com.campamento.fotos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Información de un día del campamento")
public class DayInfoResponse {

    @Schema(description = "Número de día", example = "1")
    private Integer day;

    @Schema(description = "Estado del día: ACTIVE (hay retos abiertos), COMPLETED (todos vencidos)", example = "COMPLETED")
    private String status; // ACTIVE, COMPLETED

    @Schema(description = "Cantidad de retos en este día", example = "10")
    private Integer challengeCount;

    @Schema(description = "Total de fotos subidas en este día", example = "45")
    private Integer submissionCount;
}
