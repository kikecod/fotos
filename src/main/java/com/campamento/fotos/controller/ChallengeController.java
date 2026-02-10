package com.campamento.fotos.controller;

import com.campamento.fotos.dto.ChallengeRequest;
import com.campamento.fotos.dto.ChallengeResponse;
import com.campamento.fotos.dto.DayInfoResponse;
import com.campamento.fotos.entity.User;
import com.campamento.fotos.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
@Tag(name = "Retos (Challenges)", description = "Gestión de retos fotográficos diarios del campamento")
public class ChallengeController {

    private final ChallengeService challengeService;

    @Operation(summary = "Crear un nuevo reto", description = "Solo ADMIN. Crea un reto fotográfico con título, descripción, hora de inicio y hora límite.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No tienes permisos de ADMIN")
    })
    @PostMapping
    public ResponseEntity<ChallengeResponse> create(
            @Valid @RequestBody ChallengeRequest request) {
        return ResponseEntity.ok(challengeService.create(request));
    }

    @Operation(summary = "Obtener retos del día", description = "Trae los retos de un día. Sin auth → solo status PENDING/EXPIRED. Con auth → incluye COMPLETED si ya subiste foto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de retos del día")
    })
    @GetMapping
    public ResponseEntity<List<ChallengeResponse>> getByDay(
            @Parameter(description = "Número de día del campamento", example = "1") @RequestParam Integer day,
            @AuthenticationPrincipal User user // null si no está autenticado
    ) {
        return ResponseEntity.ok(challengeService.getByDay(day, user));
    }

    @Operation(summary = "Días disponibles", description = "Lista todos los días que tienen retos. Indica si el día está ACTIVE o COMPLETED (todos los retos vencidos → galería disponible).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de días con su estado")
    })
    @GetMapping("/days")
    public ResponseEntity<List<DayInfoResponse>> getAvailableDays() {
        return ResponseEntity.ok(challengeService.getAvailableDays());
    }

    @Operation(summary = "Obtener un reto por ID", description = "Obtiene los detalles de un reto específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reto encontrado"),
            @ApiResponse(responseCode = "404", description = "Reto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponse> getById(
            @Parameter(description = "ID del reto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getById(id));
    }

    @Operation(summary = "Actualizar un reto", description = "Solo ADMIN. Actualiza los datos de un reto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No tienes permisos de ADMIN"),
            @ApiResponse(responseCode = "404", description = "Reto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeResponse> update(
            @Parameter(description = "ID del reto", example = "1") @PathVariable Long id,
            @Valid @RequestBody ChallengeRequest request) {
        return ResponseEntity.ok(challengeService.update(id, request));
    }

    @Operation(summary = "Eliminar un reto", description = "Solo ADMIN. Elimina un reto y todas sus fotos asociadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reto eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "No tienes permisos de ADMIN"),
            @ApiResponse(responseCode = "404", description = "Reto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del reto", example = "1") @PathVariable Long id) {
        challengeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
