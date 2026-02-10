package com.campamento.fotos.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "challenge_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Foto subida por un usuario como respuesta a un reto")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la submission", example = "1")
    private Long idSubmission;

    @Column(nullable = false)
    @Schema(description = "URL de la imagen subida", example = "https://storage.example.com/fotos/reto1-user1.jpg")
    private String imageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "Fecha y hora de carga automática", example = "2026-02-10T07:30:00")
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Usuario que subió la foto")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "challenge_id", nullable = false)
    @Schema(description = "Reto al que pertenece esta foto")
    private Challenge challenge;
}
