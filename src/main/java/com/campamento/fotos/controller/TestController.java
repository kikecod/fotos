package com.campamento.fotos.controller;

import com.campamento.fotos.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Endpoints de prueba para verificar la autenticación JWT")
public class TestController {

    @Operation(summary = "Health check", description = "Endpoint público para verificar que la API está funcionando. Usado por Render para health checks.")
    @ApiResponse(responseCode = "200", description = "API funcionando correctamente")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "campamento-fotos-api"
        ));
    }

    @Operation(summary = "Obtener usuario actual", description = "Devuelve la información del usuario autenticado. Requiere un token JWT válido en el header Authorization.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos del usuario autenticado"),
            @ApiResponse(responseCode = "403", description = "Token JWT inválido o ausente")
    })
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal User user) {
        Map<String, Object> response = Map.of(
                "id", user.getIdUser(),
                "username", user.getUsername(),
                "role", user.getRole().name());
        return ResponseEntity.ok(response);
    }
}
