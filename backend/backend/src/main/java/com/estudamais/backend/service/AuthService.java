package com.estudamais.backend.service;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.LoginRequest;
import com.estudamais.backend.request.RegisterRequest;
import com.estudamais.backend.request.UpdateUserRequest;
import com.estudamais.backend.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest data);
    void register(RegisterRequest data);
    void updateProfile(Long userId, UpdateUserRequest data);
    void deleteAccount(Long userId);
}
