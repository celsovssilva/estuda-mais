package com.estudamais.backend.response;

import com.estudamais.backend.entity.ChecklistTask;
import java.time.LocalDate;

public record ChecklistTaskResponse(Long id, String description, boolean completed, LocalDate executionDate) {
    public ChecklistTaskResponse(ChecklistTask task) {
        this(task.getId(), task.getDescription(), task.isCompleted(), task.getExecutionDate());
    }
}
