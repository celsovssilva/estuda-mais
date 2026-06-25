package com.estudamais.backend.controller;

import com.estudamais.backend.entity.ScheduleType;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.ScheduleRequest;
import com.estudamais.backend.response.ScheduleResponse;
import com.estudamais.backend.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/create")
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest request, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        ScheduleResponse response = scheduleService.createSchedule(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/getByUser")
    public  ResponseEntity<List<ScheduleResponse>> getByUser(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<ScheduleResponse> response = scheduleService.getSchedulesByUser(user.getId());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getByTypeSchedule")
    public ResponseEntity<List<ScheduleResponse>> byDate(Authentication authentication, @RequestParam ScheduleType type) {
        User user = (User) authentication.getPrincipal();
        List<ScheduleResponse> response = scheduleService.getSchedulesByUserAndType(user.getId(),type);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable Long scheduleId, @RequestBody @Valid ScheduleRequest request, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        ScheduleResponse response = scheduleService.updateSchedule(user.getId(), scheduleId,request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<Void> delete(@PathVariable Long scheduleId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.deleteSchedule(user.getId(), scheduleId);
        return ResponseEntity.noContent().build();
    }
}
