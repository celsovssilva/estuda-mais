package com.estudamais.backend.repository;

import com.estudamais.backend.entity.ChecklistTask;
import com.estudamais.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChecklistTaskRepository extends JpaRepository<ChecklistTask,Long> {
    List<ChecklistTask> findByUserId(Long userId);
    List<ChecklistTask> findByUserIdAndExecutionDate(Long userId, LocalDate executionDate);

    void delete(User u, ChecklistTask c);
}
