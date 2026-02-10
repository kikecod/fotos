package com.campamento.fotos.service;

import com.campamento.fotos.dto.SubmissionResponse;
import com.campamento.fotos.entity.Challenge;
import com.campamento.fotos.entity.Submission;
import com.campamento.fotos.entity.User;
import com.campamento.fotos.exception.ApiException;
import com.campamento.fotos.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ChallengeService challengeService;
    private final StorageService storageService;
    private final java.time.Clock clock;

    /**
     * USER sube una foto a un reto.
     * Validaciones:
     * 1. ¿Existe el challenge?
     * 2. ¿Ya subió foto? (duplicados)
     * 3. ¿Antes del deadline?
     */
    @Transactional
    public SubmissionResponse submit(Long challengeId, MultipartFile file, User user) {
        Challenge challenge = challengeService.findByIdOrThrow(challengeId);

        if (submissionRepository.existsByUserAndChallenge(user, challenge)) {
            throw ApiException.conflict("Ya subiste una foto para este reto. Usa PUT para actualizarla.");
        }

        if (LocalDateTime.now(clock).isAfter(challenge.getLimitTime())) {
            throw ApiException.badRequest("⏰ Tiempo agotado. La hora límite era: " + challenge.getLimitTime());
        }

        if (file.isEmpty()) {
            throw ApiException.badRequest("El archivo está vacío");
        }

        String folder = "challenges/" + challengeId;
        String imageUrl = storageService.store(file, folder);

        var submission = Submission.builder()
                .imageUrl(imageUrl)
                .user(user)
                .challenge(challenge)
                .build();

        submission = submissionRepository.save(submission);
        return toResponse(submission);
    }

    /**
     * USER actualiza su foto de un reto (antes del deadline).
     * Reemplaza el archivo anterior.
     */
    @Transactional
    public SubmissionResponse update(Long challengeId, MultipartFile file, User user) {
        Challenge challenge = challengeService.findByIdOrThrow(challengeId);

        // Buscar la submission existente
        Submission submission = submissionRepository.findByUserAndChallenge(user, challenge)
                .orElseThrow(() -> ApiException.notFound("No tienes una foto subida para este reto"));

        // Verificar deadline
        if (LocalDateTime.now(clock).isAfter(challenge.getLimitTime())) {
            throw ApiException.badRequest("⏰ Tiempo agotado. Ya no puedes modificar tu foto.");
        }

        if (file.isEmpty()) {
            throw ApiException.badRequest("El archivo está vacío");
        }

        // Eliminar archivo anterior
        storageService.delete(submission.getImageUrl());

        // Subir nuevo archivo
        String folder = "challenges/" + challengeId;
        String newImageUrl = storageService.store(file, folder);

        // Actualizar en BD
        submission.setImageUrl(newImageUrl);
        submission = submissionRepository.save(submission);

        return toResponse(submission);
    }

    /**
     * USER ve sus propias fotos subidas.
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getMySubmissions(User user) {
        return submissionRepository.findByUser(user).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * ADMIN ve todas las fotos.
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getAllSubmissions() {
        return submissionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * PÚBLICO: Ve las fotos de un reto SOLO si el tiempo ya expiró.
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getPublicSubmissions(Long challengeId) {
        Challenge challenge = challengeService.findByIdOrThrow(challengeId);

        if (LocalDateTime.now(clock).isBefore(challenge.getLimitTime())) {
            throw ApiException
                    .forbidden("Las fotos de este reto aún no son públicas. Espera hasta: " + challenge.getLimitTime());
        }

        return submissionRepository.findByChallenge(challenge).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * PÚBLICO: Galería completa de un día.
     * Solo muestra fotos de retos vencidos de ese día.
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getGalleryByDay(Integer dayNumber) {
        List<Challenge> expiredChallenges = challengeService.getExpiredChallengesByDay(dayNumber);

        if (expiredChallenges.isEmpty()) {
            return List.of(); // No hay retos vencidos aún
        }

        return submissionRepository.findByChallengeIn(expiredChallenges).stream()
                .map(this::toResponse)
                .toList();
    }

    private SubmissionResponse toResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getIdSubmission())
                .imageUrl(submission.getImageUrl())
                .uploadedAt(submission.getUploadedAt())
                .username(submission.getUser().getUsername())
                .challengeId(submission.getChallenge().getIdChallenge())
                .challengeTitle(submission.getChallenge().getTitle())
                .build();
    }
}
