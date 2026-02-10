package com.campamento.fotos.dto;

import com.campamento.fotos.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos para registrar un nuevo usuario")
public class RegisterRequest {

    @Schema(description = "Nombre de usuario único", example = "juanperez")
    private String username;

    @Schema(description = "Contraseña (mínimo 6 caracteres recomendado)", example = "miPassword123")
    private String password;

    @Schema(description = "Rol del usuario: ADMIN (crea retos) o USER (sube fotos)", example = "USER")
    private Role role;
}
