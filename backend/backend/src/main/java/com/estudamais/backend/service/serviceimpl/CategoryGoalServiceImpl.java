package com.estudamais.backend.service.serviceimpl;


import com.estudamais.backend.entity.CategoryGoal;
import com.estudamais.backend.repository.CategoryGoalRepository;
import com.estudamais.backend.request.CategoryGoalRequest;
import com.estudamais.backend.response.CategoryGoalResponse;
import com.estudamais.backend.service.CategoryGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryGoalServiceImpl implements CategoryGoalService {
    @Autowired
    private CategoryGoalRepository categoryGoalRepository;
    @Override
    public List<CategoryGoalResponse> getGoalsByuser(Long userId) {
        return categoryGoalRepository.findByUserId(userId).stream()
                .map(CategoryGoalResponse::new).toList();
    }

    @Override
    public CategoryGoalResponse saveOrUpdateGoal(Long userId, CategoryGoalRequest request) {
        Optional<CategoryGoal> existing = categoryGoalRepository.findByUserIdAndCategory(userId, request.category());

        CategoryGoal goal;
        if (existing.isPresent()) {
            goal = existing.get() ;
            goal.setWeeklyTargetMinutes(request.weeklyTargetMinutes());
        } else {
            goal = CategoryGoal.builder()
                    .userId(userId)
                .category(request.category())
                .weeklyTargetMinutes(request.weeklyTargetMinutes())
                .build();
        }

        return new CategoryGoalResponse(categoryGoalRepository.save(goal));
    }
}
