package com.estudamais.backend.request;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.ScheduleCategory;
import com.estudamais.backend.entity.ScheduleType;
import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleRequest(String title,
                              String description,
                              LocalDate targetDate,
                              ScheduleType type,
                              LocalTime startTime,
                              LocalTime endTime,
                              ScheduleCategory category) { }