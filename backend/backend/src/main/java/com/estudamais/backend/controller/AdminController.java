package com.estudamais.backend.controller;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.AdminUpdateUser;
import com.estudamais.backend.response.ScheduleResponse;
import com.estudamais.backend.response.UserResponse;
import com.estudamais.backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;


    @GetMapping("/getAll")
    public ResponseEntity<List<UserResponse>> listAll(){
        return ResponseEntity.ok(adminService.getAllUser());
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody Long userId, AdminUpdateUser request, Authentication authenticationManager){
        adminService.updateUserAdmin(userId,request);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/users/{userId}/toggle")
    public ResponseEntity<UserResponse> toggleUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.toggleUserEnabled(userId));
    }

    @GetMapping("/users/{userId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> getUserSchedules(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getScheduleByUser(userId));
    }
}
