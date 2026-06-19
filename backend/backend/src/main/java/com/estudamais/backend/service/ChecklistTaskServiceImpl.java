package com.estudamais.backend.service;

import com.estudamais.backend.entity.ChecklistTask;
import com.estudamais.backend.repository.ChecklistTaskRepository;
import com.estudamais.backend.request.ChecklistTaskRequest;
import com.estudamais.backend.response.ChecklistTaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChecklistTaskServiceImpl implements  ChecklistTaskService{
    @Autowired
    private ChecklistTaskRepository taskRepository;
    @Override
    public ChecklistTaskResponse createTask(Long userId, ChecklistTaskRequest request) {
        ChecklistTask task = ChecklistTask.builder()
                .userId(userId)
                .description(request.description())
                .executionDate(request.executionDate())
                .build();
        return new ChecklistTaskResponse(taskRepository.save(task));
    }

    @Override
    public List<ChecklistTaskResponse> getTasksByUser(Long userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(ChecklistTaskResponse::new)
                .toList();
    }

    @Override
    public List<ChecklistTaskResponse> getTasksByDate(Long userId, LocalDate date) {
        return taskRepository.findByUserIdAndExecutionDate(userId, date).stream()
                .map(ChecklistTaskResponse::new)
                .toList();
    }

    @Override
    public ChecklistTaskResponse toggleTaskCompletion(Long userId, Long taskId) {
        ChecklistTask task = taskRepository.findById(taskId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setCompleted(!task.isCompleted());
        return new ChecklistTaskResponse(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long userId, Long taskId) {
        ChecklistTask task = taskRepository.findById(taskId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }
}
