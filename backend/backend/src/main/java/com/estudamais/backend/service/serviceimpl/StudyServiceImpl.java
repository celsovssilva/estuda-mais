package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.StudyGoal;
import com.estudamais.backend.entity.StudySession;
import com.estudamais.backend.repository.StudyGoalRepository;
import com.estudamais.backend.repository.StudySessionRepository;
import com.estudamais.backend.request.GoalRequest;
import com.estudamais.backend.request.StudySessionRequest;
import com.estudamais.backend.response.DashboardResponse;
import com.estudamais.backend.response.GoalResponse;
import com.estudamais.backend.response.StudySessionResponse;
import com.estudamais.backend.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudySessionRepository sessionRepository;
    private final StudyGoalRepository goalRepository;

    private static final int MAX_HEALTHY_MINUTES_PER_DAY = 480;

    @Override
    public StudySessionResponse registerSession(Long userId, StudySessionRequest request) {
        StudySession session = StudySession.builder()
                .userId(userId)
                .subject(request.subject())
                .durationMinutes(request.durationMinutes())
                .studyDate(LocalDate.now())
                .build();

        sessionRepository.save(session);

        Integer totalTimeToday = sessionRepository.getTotalStudyTimeToday(userId, LocalDate.now());
        if (totalTimeToday == null) totalTimeToday = 0;

        String feedback = "Sessão computada com sucesso!";

        // Verifica limite de saúde mental/física do sistema
        if (totalTimeToday > MAX_HEALTHY_MINUTES_PER_DAY) {
            feedback = "AVISO DE DESCANSO: Você já ultrapassou o limite saudável de 8 horas de estudo hoje. Pare um pouco, hidrate-se e descanse a mente!";
        } else {
            // Verifica se bateu a meta pessoal customizada do dia
            var personalGoal = goalRepository.findByUserId(userId);
            if (personalGoal.isPresent() && totalTimeToday >= personalGoal.get().getTargetMinutesPerDay()) {
                feedback = "Parabéns! Você atingiu sua meta diária de estudos estipulada!";
            }
        }

        return new StudySessionResponse(session,feedback);
    }

    @Override
    public List<StudySessionResponse> getUserHistory(Long userId) {
        return sessionRepository.findAllByUserId(userId).stream()
                .map(StudySessionResponse::new).toList();
    }

    @Override
    public GoalResponse saveOrUpdateGoal(Long userId, GoalRequest request) {
        StudyGoal goal = goalRepository.findByUserId(userId)
                .orElse(StudyGoal.builder().userId(userId).build());

        goal.setCategory(request.category());
        goal.setTargetMinutesPerDay(request.targetMinutesPerDay());
        goalRepository.save(goal);

        return new GoalResponse(goal);
    }

    @Override
    public GoalResponse getGoal(Long userId) {
        StudyGoal goal = goalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Nenhuma meta configurada para este aluno."));
        return new GoalResponse(goal);
    }

    @Override
    public DashboardResponse getDashboardStats(Long userId) {
        List<StudySession> allSessions = sessionRepository.findAllByUserId(userId);
        Integer totalMinutes= allSessions.stream().mapToInt(StudySession::getDurationMinutes)
                .sum();
        Long totalSessions = (long) allSessions.size();
        List<Object[]> groupedResults = sessionRepository.getMinutesGroupedBySubject(userId);
        Map<String,Integer> minutsBySubject = groupedResults.stream()
                .collect(Collectors.toMap(result ->(String) result[0],
                        result -> ((Number) result[1]).intValue()));
        return new DashboardResponse(totalMinutes, totalSessions, minutsBySubject);
    }
}