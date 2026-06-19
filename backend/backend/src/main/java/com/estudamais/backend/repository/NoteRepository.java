package com.estudamais.backend.repository;

import com.estudamais.backend.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface NoteRepository extends JpaRepository<Note,Long> {
    List<Note> findByUserId (Long userId);
    List<Note> findByUserAndReferenceDate(Long id, LocalDate referenceDate);
}
