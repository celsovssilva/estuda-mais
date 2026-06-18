package com.estudamais.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "study_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private LocalDate studyDate;
}
