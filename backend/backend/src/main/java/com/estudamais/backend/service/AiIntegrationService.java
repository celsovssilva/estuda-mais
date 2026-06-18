package com.estudamais.backend.service;


import com.estudamais.backend.request.ReviewImportRequest;
import com.estudamais.backend.response.ReviewResponse;

import java.util.List;

public interface AiIntegrationService {
    ReviewResponse importAndProcessMaterial(Long userId, ReviewImportRequest request);
    List<ReviewResponse> getUserReviews(Long userId);
    String exportReviewText(Long userId, Long reviewId);
}