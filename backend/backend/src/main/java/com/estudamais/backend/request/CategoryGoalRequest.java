package com.estudamais.backend.request;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.ScheduleCategory;

public record CategoryGoalRequest(
        ScheduleCategory category,
        int weeklyTargetMinutes
) {
}
