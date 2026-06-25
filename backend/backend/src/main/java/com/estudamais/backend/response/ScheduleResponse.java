package com.estudamais.backend.response;

import com.estudamais.backend.entity.Schedule;
import java.time.LocalDate;

public record ScheduleResponse(Long id, String title, String description, LocalDate targetDate, String type) {

    public ScheduleResponse(Schedule schedule) {
        this(schedule.getId(), schedule.getTitle(), schedule.getDescription(), schedule.getTargetDate(), schedule.getType() != null ? schedule.getType().name() : "DAY");
    }
}
