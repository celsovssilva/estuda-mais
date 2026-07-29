package com.estudamais.backend.response;

import com.estudamais.backend.entity.CategoryGoal;

public record CategoryGoalResponse(Long id, String category, int weeklyTargetMinutes) {
    public CategoryGoalResponse(CategoryGoal goal){
            this(
                    goal.getId(),
                    goal.getCategory().name(),
                    goal.getWeeklyTargetMinutes()
            );
    }
}
