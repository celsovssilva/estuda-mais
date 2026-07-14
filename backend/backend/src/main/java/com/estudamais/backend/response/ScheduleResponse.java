package com.estudamais.backend.response;

import com.estudamais.backend.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleResponse(Long id, String title, String description, LocalDate targetDate, String type, LocalTime startTime, LocalTime endTime, boolean completed) {

    public ScheduleResponse(Schedule schedule) {
        this(schedule.getId(), schedule.getTitle(), schedule.getDescription(), schedule.getTargetDate(), schedule.getType() != null ? schedule.getType().name() : "DAY", schedule.getStartTime(), schedule.getEndTime(), schedule.isCompleted());
    }
}