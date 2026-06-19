package com.estudamais.backend.response;

import com.estudamais.backend.entity.StudyGoal;
import lombok.*;

public record GoalResponse(Long id, String category, Integer targetMinutesPerDay) {

    public GoalResponse(StudyGoal goal) {
        this(goal.getId(), goal.getCategory(), goal.getTargetMinutesPerDay());
    }
}