package com.estudamais.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespostaAluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "questao_id")
    private Question questao;
    private String alternativaEscolhida;
    private Boolean correta;

    public RespostaAluno(Question questao, String s, boolean acertou) {
    }
}
