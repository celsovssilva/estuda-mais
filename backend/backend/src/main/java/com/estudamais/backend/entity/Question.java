package com.estudamais.backend.entity;

import jakarta.persistence.*;
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


    @Column(columnDefinition = "TEXT")
    @ElementCollection
    @OrderColumn
    private List<String> alternativas;
    @Column(columnDefinition = "TEXT")
    private String respostaCorreta;
    private int numero;
    @Column(columnDefinition ="VARCHAR(20)")
    private String idioma;
    @Column(name = "dia")
    private DiaProva dia;

    private Double parametroB; // Dificuldade da questão
    private Double parametroA; // capacidade de diferenciar quem sabe
    private Double parametroC; // probabilidade de acerto

}
