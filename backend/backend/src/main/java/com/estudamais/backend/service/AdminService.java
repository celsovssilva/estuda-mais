package com.estudamais.backend.service;

import com.estudamais.backend.request.AdminUpdateUser;
import com.estudamais.backend.response.ScheduleResponse;
import com.estudamais.backend.response.UserResponse;

import java.util.List;

public interface AdminService {
    List<UserResponse> getAllUser();
    UserResponse updateUserAdmin(Long userId, AdminUpdateUser request);
    UserResponse toggleUserEnabled(Long userId);
    List<ScheduleResponse> getScheduleByUser(Long userId);
}
