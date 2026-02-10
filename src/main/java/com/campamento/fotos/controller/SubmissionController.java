package com.campamento.fotos.controller;

import com.campamento.fotos.dto.SubmissionResponse;
import com.campamento.fotos.entity.User;
import com.campamento.fotos.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Submissions (Fotos)", description = "Subida y consulta de fotos de los retos")
public class SubmissionController {

        private final SubmissionService submissionService;

        @Operation(summary = "Subir foto a un reto", description = """
                        Solo USER. Sube una foto como respuesta a un reto.

                        **Validaciones:**
                        - El reto debe existir
                        - No puedes subir más de una foto por reto
                        - Debe ser antes de la hora límite
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Foto subida exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Tiempo agotado o archivo vacío"),
                        @ApiResponse(responseCode = "404", description = "Reto no encontrado"),
                        @ApiResponse(responseCode = "409", description = "Ya subiste una foto para este reto")
        })
        @PostMapping(value = "/api/challenges/{id}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<SubmissionResponse> submit(
                        @Parameter(description = "ID del reto") @PathVariable Long id,
                        @Parameter(description = "Archivo de imagen (JPG, PNG)") @RequestParam("file") MultipartFile file,
                        @AuthenticationPrincipal User user) {
                return ResponseEntity.ok(submissionService.submit(id, file, user));
        }

        @Operation(summary = "Actualizar foto de un reto", description = """
                        Solo USER. Reemplaza tu foto actual por una nueva.

                        **Validaciones:**
                        - El reto debe existir
                        - Debes tener una foto previa subida
                        - Debe ser antes de la hora límite
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Foto actualizada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Tiempo agotado o archivo vacío"),
                        @ApiResponse(responseCode = "404", description = "Reto o submission no encontrado")
        })
        @PutMapping(value = "/api/challenges/{id}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<SubmissionResponse> update(
                        @Parameter(description = "ID del reto") @PathVariable Long id,
                        @Parameter(description = "Nuevo archivo de imagen") @RequestParam("file") MultipartFile file,
                        @AuthenticationPrincipal User user) {
                return ResponseEntity.ok(submissionService.update(id, file, user));
        }

        @Operation(summary = "Ver mis fotos subidas", description = "Solo USER. Muestra todas las fotos que has subido a los diferentes retos.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de tus fotos")
        })
        @GetMapping("/api/submissions/my")
        public ResponseEntity<List<SubmissionResponse>> getMySubmissions(
                        @AuthenticationPrincipal User user) {
                return ResponseEntity.ok(submissionService.getMySubmissions(user));
        }

        @Operation(summary = "Ver todas las fotos (Admin)", description = "Solo ADMIN. Muestra todas las fotos de todos los usuarios para proyectar o evaluar.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de todas las fotos"),
                        @ApiResponse(responseCode = "403", description = "No tienes permisos de ADMIN")
        })
        @GetMapping("/api/submissions/all")
        public ResponseEntity<List<SubmissionResponse>> getAllSubmissions() {
                return ResponseEntity.ok(submissionService.getAllSubmissions());
        }

        @Operation(summary = "Galería pública de un reto", description = "Sin autenticación. Muestra las fotos de un reto SOLO después de que el tiempo límite haya pasado.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Fotos del reto (público)"),
                        @ApiResponse(responseCode = "403", description = "El tiempo aún no ha expirado"),
                        @ApiResponse(responseCode = "404", description = "Reto no encontrado")
        })
        @GetMapping("/api/submissions/public")
        public ResponseEntity<List<SubmissionResponse>> getPublicSubmissions(
                        @Parameter(description = "ID del reto", example = "1") @RequestParam Long challengeId) {
                return ResponseEntity.ok(submissionService.getPublicSubmissions(challengeId));
        }

        @Operation(summary = "Galería pública por día", description = "Sin autenticación. Muestra TODAS las fotos de un día donde los retos ya vencieron. Ideal para la galería del frontend.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Fotos del día (público)")
        })
        @GetMapping("/api/gallery")
        public ResponseEntity<List<SubmissionResponse>> getGalleryByDay(
                        @Parameter(description = "Número de día del campamento", example = "1") @RequestParam Integer day) {
                return ResponseEntity.ok(submissionService.getGalleryByDay(day));
        }
}
