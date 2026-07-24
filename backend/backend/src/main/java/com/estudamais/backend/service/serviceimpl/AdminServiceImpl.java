package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.repository.SheduleRepository;
import com.estudamais.backend.repository.UserRepository;
import com.estudamais.backend.request.AdminUpdateUser;
import com.estudamais.backend.response.ScheduleResponse;
import com.estudamais.backend.response.UserResponse;
import com.estudamais.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SheduleRepository sheduleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUser() {
        return userRepository.findAll().stream()
                .map(UserResponse::new).toList();
    }

    @Override
    public UserResponse updateUserAdmin(Long userId, AdminUpdateUser request) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User não existe"));
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        return new UserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse toggleUserEnabled(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User não existe"));
        user.setEnabled(!user.isEnabled());
        return new UserResponse(userRepository.save(user));
    }

    @Override
    public List<ScheduleResponse> getScheduleByUser(Long userId) {
      List<Schedule> scheduleResponses = sheduleRepository.findByUserId(userId);
      return scheduleResponses.stream().map(ScheduleResponse::new).toList();
    }


}
