package com.estudamais.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentSimulation {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuarioId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Double notaCalculadaTRI;

    @OneToMany( cascade = CascadeType.ALL)
    private List<RespostaAluno> respostas = new ArrayList<>();
}
