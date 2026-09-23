package com.example.ecommerce.recommendation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService
    ) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/users/{userId}")
    public List<RecommendationResponse> getRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit
    ) {

        return recommendationService.getRecommendationsForUser(
                userId,
                limit
        );
    }
}