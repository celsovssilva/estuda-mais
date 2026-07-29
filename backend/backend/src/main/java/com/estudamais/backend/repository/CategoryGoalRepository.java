package com.estudamais.backend.repository;

import com.estudamais.backend.entity.CategoryGoal;
import com.estudamais.backend.entity.ScheduleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryGoalRepository extends JpaRepository<CategoryGoal,Long> {
    List<CategoryGoal> findByUserId(Long userId);

    Optional<CategoryGoal> findByUserIdAndCategory(Long userId, ScheduleCategory category);
}

