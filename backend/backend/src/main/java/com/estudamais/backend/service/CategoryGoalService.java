package com.estudamais.backend.service;

import com.estudamais.backend.request.CategoryGoalRequest;
import com.estudamais.backend.response.CategoryGoalResponse;

import java.util.List;

public interface CategoryGoalService {
    List<CategoryGoalResponse> getGoalsByuser(Long userId);
    CategoryGoalResponse saveOrUpdateGoal(Long userId, CategoryGoalRequest request);
}
