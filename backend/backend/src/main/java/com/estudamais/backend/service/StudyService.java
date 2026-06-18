package com.estudamais.backend.service;
import com.estudamais.backend.request.GoalRequest;
import com.estudamais.backend.request.StudySessionRequest;
import com.estudamais.backend.response.GoalResponse;
import com.estudamais.backend.response.StudySessionResponse;

import java.util.List;

public interface StudyService {
    StudySessionResponse registerSession(Long userId, StudySessionRequest request);
    List<StudySessionResponse> getUserHistory(Long userId);
    GoalResponse saveOrUpdateGoal(Long userId, GoalRequest request);
    GoalResponse getGoal(Long userId);
}