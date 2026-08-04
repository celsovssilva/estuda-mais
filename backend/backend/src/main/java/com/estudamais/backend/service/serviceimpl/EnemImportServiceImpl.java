package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.Question;
import com.estudamais.backend.repository.QuestionRepository;
import com.estudamais.backend.response.QuestionResponse;
import com.estudamais.backend.service.EnemImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EnemImportServiceImpl implements EnemImportService {
    private final RestClient restClient = RestClient.create();
    @Autowired
    private QuestionRepository questaoRepository;

    @Override
    public void importarProvas(int ano) {
        QuestionResponse[] questoesApi = restClient.get()
                .uri("https://api.enem.dev/v1/exams/{year}/questions?limit=100", ano)
                .retrieve()
                .body(QuestionResponse[].class);

        for (QuestionResponse dto : questoesApi) {
            Question q = new Question();
            q.setId(dto.id());
            q.setAno(ano);
            q.setDisciplina(dto.disciplina());
            q.setEnunciado(dto.enunciado());
            q.setAlternativas(dto.alternativas());
            q.setRespostaCorreta(dto.respostaCorreta());

            // Estimativa inicial de dificuldade para TRI (fácil=0, média=1, difícil=2)
            q.setParametroB(1.0);
            q.setParametroA(1.5);
            q.setParametroC(0.20);

            questaoRepository.save(q);
        }
    }
}
