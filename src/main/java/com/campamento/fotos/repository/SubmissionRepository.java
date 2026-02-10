package com.campamento.fotos.repository;

import com.campamento.fotos.entity.Challenge;
import com.campamento.fotos.entity.Submission;
import com.campamento.fotos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUser(User user);

    List<Submission> findByChallenge(Challenge challenge);

    boolean existsByUserAndChallenge(User user, Challenge challenge);

    Optional<Submission> findByUserAndChallenge(User user, Challenge challenge);

    List<Submission> findByChallengeIn(List<Challenge> challenges);

    long countByChallenge(Challenge challenge);
}
