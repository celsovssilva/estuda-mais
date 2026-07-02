package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.ChecklistTask;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.repository.ChecklistTaskRepository;
import com.estudamais.backend.repository.UserRepository;
import com.estudamais.backend.request.ChecklistTaskRequest;
import com.estudamais.backend.response.ChecklistTaskResponse;
import com.estudamais.backend.service.ChecklistTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChecklistTaskImpl implements ChecklistTaskService {
    @Autowired
    private ChecklistTaskRepository checklistTaskRepository;
    @Autowired
    private UserRepository userRepository;
        @Override
        public ChecklistTaskResponse createTask(Long userId, ChecklistTaskRequest request) {
            ChecklistTask t = new ChecklistTask();
            t.setExecutionDate(request.executionDate());
            t.setDescription(request.description());

            User user = new User();
            user.setId(userId);
          t.setUser(user);


            ChecklistTask savedTask = checklistTaskRepository.save(t);
            return new ChecklistTaskResponse(
                    savedTask.getId(),
                    savedTask.getDescription(),
                    savedTask.isCompleted(),
                    savedTask.getExecutionDate()
            );
        }

    @Override
    public List<ChecklistTaskResponse> getTasksByUser(Long userId) {
        List<ChecklistTask> c = checklistTaskRepository.findTasksByUserId(userId);

        return c.stream().map(t -> new ChecklistTaskResponse(
                t.getUser().getId(),
                t.getDescription(),
                t.isCompleted(),
                t.getExecutionDate()
        )).toList();
    }

    @Override
    public List<ChecklistTaskResponse> getTasksByDate(Long userId, LocalDate date) {
        List<ChecklistTask> c = checklistTaskRepository.findTasksByUserIdAndDate(userId, date);
        return c.stream().map(t -> new ChecklistTaskResponse(
                t.getUser().getId(),
                t.getDescription(),
                t.isCompleted(),
                t.getExecutionDate()
        )).toList();
    }

    @Override
    public ChecklistTaskResponse toggleTaskCompletion(Long userId, Long taskId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ChecklistTask c = checklistTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));


        if (!c.getUser().getId().equals(userId)) {
            throw new RuntimeException("Acesso negado: esta tarefa não pertence a este usuário.");
        }
        c.setCompleted(!c.isCompleted());
        ChecklistTask updatedTask = checklistTaskRepository.save(c);
        return new ChecklistTaskResponse(
                updatedTask.getId(),
                updatedTask.getDescription(),
                updatedTask.isCompleted(),
                updatedTask.getExecutionDate()
        );
    }

    @Override
    public void deleteTask(Long userId, Long taskId) {
        User u = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("user não encontrado"));
        ChecklistTask c = checklistTaskRepository.findById(taskId)
                        .orElseThrow(()-> new RuntimeException("task não encotrada"));

            checklistTaskRepository.delete(c);
    }
}
