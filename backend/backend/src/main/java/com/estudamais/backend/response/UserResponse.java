package com.estudamais.backend.response;

import com.estudamais.backend.entity.User;

public record UserResponse(Long id, String email, String role, boolean enabled) {
    public UserResponse(User user){
        this(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.isEnabled()
        );

    }
}
