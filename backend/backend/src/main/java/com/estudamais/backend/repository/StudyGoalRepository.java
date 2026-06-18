package com.estudamais.backend.repository;

import com.estudamais.backend.entity.StudyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudyGoalRepository extends JpaRepository<StudyGoal, Long> {
    Optional<StudyGoal> findByUserId(Long userId);
}