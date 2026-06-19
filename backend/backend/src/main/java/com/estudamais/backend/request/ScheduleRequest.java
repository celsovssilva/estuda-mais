package com.estudamais.backend.request;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.ScheduleType;
import java.time.LocalDate;

public record ScheduleRequest(String title,
                              String description,
                              LocalDate targetDate,
                              ScheduleType type) { }