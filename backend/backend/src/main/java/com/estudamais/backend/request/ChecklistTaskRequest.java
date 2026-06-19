package com.estudamais.backend.request;

import java.time.LocalDate;

public record ChecklistTaskRequest(String description, LocalDate executionDate) {}