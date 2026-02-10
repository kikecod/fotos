package com.campamento.fotos.controller;

import com.campamento.fotos.dto.AuthenticationRequest;
import com.campamento.fotos.dto.AuthenticationResponse;
import com.campamento.fotos.dto.RegisterRequest;
import com.campamento.fotos.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de registro e inicio de sesión. No requieren token JWT.")
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema y devuelve un token JWT para autenticación inmediata.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario existente con username y password. Devuelve un token JWT válido por 24 horas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "403", description = "Credenciales incorrectas", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
