package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.ScheduleCategory;
import com.estudamais.backend.entity.ScheduleType;
import com.estudamais.backend.repository.SheduleRepository;
import com.estudamais.backend.request.ScheduleRequest;
import com.estudamais.backend.response.CategoryMetricResponse;
import com.estudamais.backend.response.ScheduleResponse;
import com.estudamais.backend.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private SheduleRepository scheduleRepository;

    @Override
    public List<Schedule> createSchedule(Long userId, ScheduleRequest request) {
        Schedule schedule = Schedule.builder()
                .userId(userId)
                .title(request.title())
                .description(request.description())
                .targetDate(request.targetDate())
                .type(request.type())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .category(request.category())
                .build();

        LocalDate dataInicial = request.targetDate();
        LocalDate dataFinal = dataInicial;

        if (request.type() == ScheduleType.WEEK) {
            dataFinal = dataInicial.plusDays(6);
        } else if (request.type() == ScheduleType.MONTH) {
            dataFinal = dataInicial.plusMonths(1);
        }

        List<Schedule> compromissos = new ArrayList<>();

        while (!dataInicial.isAfter(dataFinal)) {
            Schedule s = Schedule.builder()
                    .userId(userId)
                    .title(request.title())
                    .description(request.description())
                    .targetDate(dataInicial)
                    .type(request.type())
                    .startTime(request.startTime())
                    .endTime(request.endTime())
                    .category(request.category())
                    .build();
            compromissos.add(s);
            dataInicial = dataInicial.plusDays(1);
        }

        return scheduleRepository.saveAll(compromissos);
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
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

             schedule.setType(request.type());
             schedule.setStartTime(request.startTime());
             schedule.setEndTime(request.endTime());
             schedule.setCategory(request.category());
             schedule.setTitle(request.title());
             schedule.setDescription(request.description());
        LocalDate dataInicial = request.targetDate();
        LocalDate dataFinal = dataInicial;

        if (request.type() == ScheduleType.WEEK) {
            dataFinal = dataInicial.plusDays(6);
        } else if (request.type() == ScheduleType.MONTH) {
            dataFinal = dataInicial.plusMonths(1);
        }
        dataInicial = dataInicial.plusDays(1);

        List<Schedule> compromissos = new ArrayList<>();
        while (!dataInicial.isAfter(dataFinal)){
            Schedule s = Schedule.builder()
                    .userId(userId)
                    .title(request.title())
                    .description(request.description())
                    .targetDate(dataInicial)
                    .type(request.type())
                    .startTime(request.startTime())
                    .endTime(request.endTime())
                    .category(request.category())
                    .build();
            dataInicial = dataInicial.plusDays(1);
            compromissos.add(s);
        }

        scheduleRepository.saveAll(compromissos);

        return new ScheduleResponse(scheduleRepository.save(schedule));
    }


    @Override
    public void deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .filter(s -> s.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        scheduleRepository.delete(schedule);
    }

    @Override
    public ScheduleResponse toggleScheduleCompletion(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Compromisso não encontrado"));

        if (!schedule.getUserId().equals(userId)) {
            throw new RuntimeException("Acesso negado: este compromisso não pertence a este usuário.");
        }

        schedule.setCompleted(!schedule.isCompleted());
        Schedule updated = scheduleRepository.save(schedule);
        return new ScheduleResponse(updated);
    }

    @Override
    public List<CategoryMetricResponse> getMonthlyMetricsByCategory(Long userId, Integer year, Integer month) {
        LocalDate now = LocalDate.now();
        int y = (year != null) ? year : now.getYear();
        int m = (month != null) ? month : now.getMonthValue();

        List<Schedule> all = scheduleRepository.findByUserId(userId);

        List<Schedule> monthSchedules = all.stream()
                .filter(s -> s.getTargetDate() != null
                        && s.getTargetDate().getYear() == y
                        && s.getTargetDate().getMonthValue() == m)
                .toList();


        Map<ScheduleCategory, List<Schedule>> grouped = monthSchedules.stream()
                .collect(Collectors.groupingBy(s ->
                        s.getCategory() != null ? s.getCategory() : ScheduleCategory.OUTROS
                ));

        List<CategoryMetricResponse> result = new ArrayList<>();

        for (Map.Entry<ScheduleCategory, List<Schedule>> entry : grouped.entrySet()) {
            List<Schedule> items = entry.getValue();
            long total = items.size();
            long completed = items.stream().filter(Schedule::isCompleted).count();
            int percentage = (total == 0) ? 0 : (int) Math.round((completed * 100.0) / total);

            result.add(new CategoryMetricResponse(
                    entry.getKey().name(),
                    total,
                    completed,
                    percentage
            ));
        }

        return result;
    }
}
