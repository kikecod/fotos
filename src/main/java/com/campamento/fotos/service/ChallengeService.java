package com.campamento.fotos.service;

import com.campamento.fotos.dto.ChallengeRequest;
import com.campamento.fotos.dto.ChallengeResponse;
import com.campamento.fotos.dto.DayInfoResponse;
import com.campamento.fotos.entity.Challenge;
import com.campamento.fotos.entity.User;
import com.campamento.fotos.exception.ApiException;
import com.campamento.fotos.repository.ChallengeRepository;
import com.campamento.fotos.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;

    /**
     * ADMIN crea un nuevo reto.
     */
    public ChallengeResponse create(ChallengeRequest request) {
        if (request.getLimitTime().isBefore(request.getStartTime())) {
            throw ApiException.badRequest("La hora límite debe ser posterior a la hora de inicio");
        }

        var challenge = Challenge.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .limitTime(request.getLimitTime())
                .dayNumber(request.getDayNumber())
                .build();

        challenge = challengeRepository.save(challenge);
        return toResponse(challenge, null);
    }

    /**
     * Obtiene los retos de un día con el estado para el usuario.
     * Si user es null (público), no calcula COMPLETED.
     */
    public List<ChallengeResponse> getByDay(Integer dayNumber, User user) {
        var challenges = challengeRepository.findByDayNumberOrderByStartTimeAsc(dayNumber);
        return challenges.stream()
                .map(c -> toResponse(c, user))
                .toList();
    }

    /**
     * Lista de días disponibles con su estado.
     * COMPLETED = todos los retos de ese día han vencido.
     * ACTIVE = al menos uno sigue abierto.
     */
    public List<DayInfoResponse> getAvailableDays() {
        var dayNumbers = challengeRepository.findDistinctDayNumbers();
        LocalDateTime now = LocalDateTime.now();

        return dayNumbers.stream().map(day -> {
            var challenges = challengeRepository.findByDayNumberOrderByStartTimeAsc(day);

            boolean allExpired = challenges.stream()
                    .allMatch(c -> now.isAfter(c.getLimitTime()));

            int totalSubmissions = challenges.stream()
                    .mapToInt(c -> c.getSubmissions() != null ? c.getSubmissions().size() : 0)
                    .sum();

            return DayInfoResponse.builder()
                    .day(day)
                    .status(allExpired ? "COMPLETED" : "ACTIVE")
                    .challengeCount(challenges.size())
                    .submissionCount(totalSubmissions)
                    .build();
        }).toList();
    }

    /**
     * Busca un challenge por ID o lanza 404.
     */
    public Challenge findByIdOrThrow(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Reto no encontrado con ID: " + id));
    }

    /**
     * Obtiene un reto por ID.
     */
    public ChallengeResponse getById(Long id) {
        var challenge = findByIdOrThrow(id);
        return toResponse(challenge, null);
    }

    /**
     * ADMIN actualiza un reto existente.
     */
    public ChallengeResponse update(Long id, ChallengeRequest request) {
        if (request.getLimitTime().isBefore(request.getStartTime())) {
            throw ApiException.badRequest("La hora límite debe ser posterior a la hora de inicio");
        }

        var challenge = findByIdOrThrow(id);
        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.setStartTime(request.getStartTime());
        challenge.setLimitTime(request.getLimitTime());
        challenge.setDayNumber(request.getDayNumber());

        challenge = challengeRepository.save(challenge);
        return toResponse(challenge, null);
    }

    /**
     * ADMIN elimina un reto.
     */
    public void delete(Long id) {
        var challenge = findByIdOrThrow(id);
        challengeRepository.delete(challenge);
    }

    /**
     * Obtiene los retos vencidos de un día (para la galería).
     */
    public List<Challenge> getExpiredChallengesByDay(Integer dayNumber) {
        LocalDateTime now = LocalDateTime.now();
        return challengeRepository.findByDayNumberOrderByStartTimeAsc(dayNumber).stream()
                .filter(c -> now.isAfter(c.getLimitTime()))
                .toList();
    }

    /**
     * Convierte entidad a DTO con estado calculado.
     */
    private ChallengeResponse toResponse(Challenge challenge, User user) {
        String status;
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(challenge.getLimitTime())) {
            status = "EXPIRED"; // Rojo
        } else if (user != null && submissionRepository.existsByUserAndChallenge(user, challenge)) {
            status = "COMPLETED"; // Verde
        } else {
            status = "PENDING"; // Gris
        }

        int submissionCount = challenge.getSubmissions() != null
                ? challenge.getSubmissions().size()
                : 0;

        return ChallengeResponse.builder()
                .id(challenge.getIdChallenge())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .startTime(challenge.getStartTime())
                .limitTime(challenge.getLimitTime())
                .dayNumber(challenge.getDayNumber())
                .status(status)
                .submissionCount(submissionCount)
                .build();
    }
}
