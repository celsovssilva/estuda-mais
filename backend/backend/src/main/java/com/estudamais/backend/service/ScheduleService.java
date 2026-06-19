package com.estudamais.backend.service;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.ScheduleType;
import com.estudamais.backend.request.ScheduleRequest;
import com.estudamais.backend.response.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    ScheduleResponse createSchedule(Long userId, ScheduleRequest request);
    List<ScheduleResponse> getSchedulesByUser(Long userId);
    List<ScheduleResponse> getSchedulesByUserAndType(Long userId, ScheduleType type);
    ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleRequest request);
    void deleteSchedule(Long userId, Long scheduleId);
}
