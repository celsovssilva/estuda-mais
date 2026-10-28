package com.estudamais.backend.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @ManyToOne
    private StudentSimulation simulation;

    public RespostaAluno(Question questao, String alternativaEscolhida, boolean correta) {
        this.questao = questao;
        this.alternativaEscolhida = alternativaEscolhida;
        this.correta = correta;
    }


}
