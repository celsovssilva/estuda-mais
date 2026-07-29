package com.estudamais.backend.controller;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.CategoryGoalRequest;
import com.estudamais.backend.response.CategoryGoalResponse;
import com.estudamais.backend.service.CategoryGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/goals")
public class CategoryGoal {
    @Autowired
    private CategoryGoalService categoryGoalService;

    @GetMapping
    public ResponseEntity<List<CategoryGoalResponse>> getByUserGoals(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(categoryGoalService.getGoalsByuser(user.getId()));
    }
    @PostMapping
    public ResponseEntity<CategoryGoalResponse> saveOrUpdate(Authentication authentication, CategoryGoalRequest request){
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(categoryGoalService.saveOrUpdateGoal(user.getId(), request));
    }

}
