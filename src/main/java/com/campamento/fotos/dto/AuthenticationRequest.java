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
@Schema(description = "Datos para iniciar sesión")
public class AuthenticationRequest {

    @Schema(description = "Nombre de usuario", example = "admin1")
    private String username;

    @Schema(description = "Contraseña del usuario", example = "admin123")
    private String password;
}