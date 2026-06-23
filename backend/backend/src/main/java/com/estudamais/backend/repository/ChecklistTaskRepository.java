package com.estudamais.backend.repository;

import com.estudamais.backend.entity.ChecklistTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChecklistTaskRepository extends JpaRepository<ChecklistTask, Long> {

    @Query("SELECT t FROM ChecklistTask t WHERE t.user.id = :userId")
    List<ChecklistTask> findTasksByUserId(@Param("userId") Long userId);
    @Query("SELECT t FROM ChecklistTask t WHERE t.user.id = :userId AND t.executionDate = :date")
    List<ChecklistTask> findTasksByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}