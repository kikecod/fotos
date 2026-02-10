package com.campamento.fotos.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Roles disponibles en el sistema")
public enum Role {
    @Schema(description = "Administrador: crea retos y gestiona el sistema")
    ADMIN,

    @Schema(description = "Usuario: sube fotos a los retos activos")
    USER
}
