package com.estudamais.backend.response;

import com.estudamais.backend.entity.Question;

import java.util.List;

public record QuestionResponse(String id, Integer ano, String disciplina, String enunciado, List<String> alternativas,String respostaCorreta) {
    public QuestionResponse(Question q){
        this (
                q.getId(),
                q.getAno(),
                q.getDisciplina(),
                q.getEnunciado(),
                q.getAlternativas(),
                q.getRespostaCorreta()
        );
    }
}
