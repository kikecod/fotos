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
@Schema(description = "Respuesta de autenticación con token JWT")
public class AuthenticationResponse {

    @Schema(description = "Token JWT para autenticación. Válido por 24 horas.", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbjEiLCJpYXQiOjE3MDk5...")
    private String token;
}
