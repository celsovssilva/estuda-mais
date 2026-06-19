package com.estudamais.backend.repository;

import com.estudamais.backend.entity.ChecklistTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ChecklistTaskRepository extends JpaRepository<ChecklistTask,Long> {
    List<ChecklistTask> findByUserId(Long userId);
    List<ChecklistTask> findByUserIdAndExecutionDate(Long userId, LocalDate executionDate);
}
