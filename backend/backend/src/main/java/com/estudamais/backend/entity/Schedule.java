package com.estudamais.backend.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tb_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;

    private LocalTime startTime;

    private LocalTime endTime;
    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean completed = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, columnDefinition = "varchar(30) default 'OUTROS'")
    private ScheduleCategory category = ScheduleCategory.OUTROS;
}