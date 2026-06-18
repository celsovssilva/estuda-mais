package com.estudamais.backend.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalResponse {
    private Long id;
    private String category;
    private Integer targetMinutesPerDay;
}