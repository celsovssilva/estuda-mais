package com.estudamais.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "study_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String category; // Ex: Concurso, ENEM, Faculdade

    @Column(nullable = false)
    private Integer targetMinutesPerDay; // Tempo que o estudante deseja estudar por dia
}
