package com.estudamais.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String rawContent; // Texto extraído de apostilas, PDFs ou transcrição

    @Column(columnDefinition = "TEXT")
    private String aiSummary; // Resumo gerado pela IA

    @Column(columnDefinition = "TEXT")
    private String aiGeneratedQuiz; // JSON com as perguntas da prova geradas pela IA

    private LocalDateTime createdAt;
}