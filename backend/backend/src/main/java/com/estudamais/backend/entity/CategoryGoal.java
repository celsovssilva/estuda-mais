package com.estudamais.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_category_goals", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "category"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleCategory category;

    @Column(nullable = false)
    private int weeklyTargetMinutes;
}
