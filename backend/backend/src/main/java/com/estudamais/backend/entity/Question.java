package com.estudamais.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    private String id;
    private Integer ano;
    private String disciplina;

    @Column(columnDefinition = "TEXT")
    private String enunciado;

    @ElementCollection
    private List<String> alternativas;
    private String respostaCorreta;


    private Double parametroB; // Dificuldade da questão
    private Double parametroA; // capacidade de diferenciar quem sabe
    private Double parametroC; // probabilidade de acerto
}
