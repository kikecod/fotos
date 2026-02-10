package com.campamento.fotos.repository;

import com.campamento.fotos.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByDayNumberOrderByStartTimeAsc(Integer dayNumber);

    @Query("SELECT DISTINCT c.dayNumber FROM Challenge c ORDER BY c.dayNumber ASC")
    List<Integer> findDistinctDayNumbers();
}
