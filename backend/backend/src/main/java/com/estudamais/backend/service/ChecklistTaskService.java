package com.estudamais.backend.service;

import com.estudamais.backend.request.ChecklistTaskRequest;
import com.estudamais.backend.response.ChecklistTaskResponse;

import java.time.LocalDate;
import java.util.List;

public interface ChecklistTaskService {
    ChecklistTaskResponse createTask(Long userId, ChecklistTaskRequest request);
    List<ChecklistTaskResponse> getTasksByUser(Long userId);
    List<ChecklistTaskResponse> getTasksByDate(Long userId, LocalDate date);
    ChecklistTaskResponse toggleTaskCompletion(Long userId, Long taskId);
    void deleteTask(Long userId, Long taskId);
}
