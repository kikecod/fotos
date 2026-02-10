package com.campamento.fotos.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenges")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(description = "Reto fotográfico diario del campamento")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del reto", example = "1")
    private Long idChallenge;

    @Column(nullable = false)
    @Schema(description = "Título del reto", example = "Foto del amanecer")
    private String title;

    @Schema(description = "Descripción detallada del reto", example = "Toma una foto del amanecer desde el campamento")
    private String description;

    @Column(name = "start_time", nullable = false)
    @Schema(description = "Hora de inicio del reto", example = "2026-02-10T06:00:00")
    private LocalDateTime startTime;

    @Column(name = "limit_time", nullable = false)
    @Schema(description = "Hora límite para subir fotos", example = "2026-02-10T08:00:00")
    private LocalDateTime limitTime;

    @Column(nullable = false)
    @Schema(description = "Número del día del campamento", example = "1")
    private Integer dayNumber;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Lista de fotos subidas para este reto")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Submission> submissions = new ArrayList<>();
}
