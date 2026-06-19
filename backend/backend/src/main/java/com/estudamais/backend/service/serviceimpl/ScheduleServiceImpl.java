package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.ScheduleType;
import com.estudamais.backend.repository.SheduleRepository;
import com.estudamais.backend.request.ScheduleRequest;
import com.estudamais.backend.response.ScheduleResponse;
import com.estudamais.backend.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private SheduleRepository scheduleRepository;

    @Override
    public ScheduleResponse createSchedule(Long userId, ScheduleRequest request) {
        Schedule schedule = Schedule.builder()
                .userId(userId)
                .title(request.title())
                .description(request.description())
                .targetDate(request.targetDate())
                .type(request.type())
                .build();
        return new ScheduleResponse(scheduleRepository.save(schedule));
    }

    @Override
    public List<ScheduleResponse> getSchedulesByUser(Long userId) {
        return scheduleRepository.findByUserId(userId).stream()
                .map(ScheduleResponse::new)
                .toList();
    }

    @Override
    public List<ScheduleResponse> getSchedulesByUserAndType(Long userId, ScheduleType type) {
        return scheduleRepository.findByUserIdAndType(userId, type).stream()
                .map(ScheduleResponse::new)
                .toList();
    }

    @Override
    public ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .filter(s -> s.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        schedule.setTitle(request.title());
        schedule.setDescription(request.description());
        schedule.setTargetDate(request.targetDate());
        schedule.setType(request.type());

        return new ScheduleResponse(scheduleRepository.save(schedule));
    }

    @Override
    public void deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .filter(s -> s.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        scheduleRepository.delete(schedule);
    }
}
