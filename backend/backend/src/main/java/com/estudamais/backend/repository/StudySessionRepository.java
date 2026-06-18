package com.estudamais.backend.repository;

import com.estudamais.backend.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    List<StudySession> findAllByUserId(Long userId);

    @Query("SELECT SUM(s.durationMinutes) FROM StudySession s WHERE s.userId = :userId AND s.studyDate = :date")
    Integer getTotalStudyTimeToday(Long userId, LocalDate date);
}