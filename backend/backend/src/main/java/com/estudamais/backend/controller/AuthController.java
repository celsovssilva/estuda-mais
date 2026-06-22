package com.estudamais.backend.controller;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.LoginRequest;
import com.estudamais.backend.request.RegisterRequest;
import com.estudamais.backend.request.UpdateUserRequest;
import com.estudamais.backend.response.AuthResponse;
import com.estudamais.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> LoginUser(@Valid @RequestBody LoginRequest data){
        return ResponseEntity.ok(authService.login(data));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> resgisterUser(@Valid @RequestBody RegisterRequest data){
        try {
            authService.register(data);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e){
            return  ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UpdateUserRequest data, Authentication authentication){
        try {
            User user = (User) authentication.getPrincipal();
            authService.updateProfile(user.getId(), data);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
