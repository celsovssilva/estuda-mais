package com.estudamais.backend.controller;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.ChecklistTaskRequest;
import com.estudamais.backend.response.ChecklistTaskResponse;
import com.estudamais.backend.service.ChecklistTaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/checklist")
public class ChecklistTaskController {
    @Autowired
    private ChecklistTaskService checklistTaskService;

    @PostMapping("/create")
    public ResponseEntity<ChecklistTaskResponse> create(@Valid @RequestBody  ChecklistTaskRequest request, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        ChecklistTaskResponse  response = checklistTaskService.createTask(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/getTasksByUser")
    public ResponseEntity<List<ChecklistTaskResponse>> getbyUserTask(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<ChecklistTaskResponse> response = checklistTaskService.getTasksByUser(user.getId());
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{taskId}/toggle")
    public ResponseEntity<ChecklistTaskResponse> toggle(@PathVariable Long taskId, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        ChecklistTaskResponse response = checklistTaskService.toggleTaskCompletion(user.getId(),taskId);
        return  ResponseEntity.ok(response);
    }
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        checklistTaskService.deleteTask(user.getId(), taskId);
        return ResponseEntity.noContent().build();
    }
}
