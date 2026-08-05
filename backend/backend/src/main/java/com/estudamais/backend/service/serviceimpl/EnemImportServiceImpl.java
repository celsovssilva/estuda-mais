package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.Question;
import com.estudamais.backend.repository.QuestionRepository;
import com.estudamais.backend.service.EnemImportService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnemImportServiceImpl implements EnemImportService {

    private final RestClient restClient = RestClient.create();

    @Autowired
    private QuestionRepository questaoRepository;

    @Override
    public void importarProvas(int ano) {
        try {
            JsonNode root = restClient.get()
                    .uri("https://api.enem.dev/v1/exams/{year}/questions", ano)
                    .retrieve()
                    .body(JsonNode.class);

            if (root != null && root.has("questions")) {
                JsonNode questionsArray = root.get("questions");
                List<Question> listaParaSalvar = new ArrayList<>();

                for (JsonNode node : questionsArray) {
                    int index = node.path("index").asInt();
                    long idCalculado = ((long) ano * 1000) + index;
                    String idString = String.valueOf(idCalculado);
                    if (!questaoRepository.existsById(Long.valueOf(idString))) {
                        Question q = new Question();
                        q.setId(idString);
                        q.setAno(ano);
                        q.setDisciplina(node.path("discipline").asText("Geral"));
                        q.setEnunciado(node.path("context").asText(""));

                        List<String> alts = new ArrayList<>();
                        for (JsonNode altNode : node.path("alternatives")) {
                            String textoAlt = altNode.path("text").asText();
                            alts.add(textoAlt);

                            if (altNode.path("isCorrect").asBoolean(false)) {
                                q.setRespostaCorreta(textoAlt);
                            }
                        }
                        q.setAlternativas(alts);

                        q.setParametroB(1.0);
                        q.setParametroA(1.5);
                        q.setParametroC(0.20);

                        listaParaSalvar.add(q);
                    }
                }

                questaoRepository.saveAll(listaParaSalvar);
            }
        } catch (Exception e) {
            System.err.println("Erro ao importar questões da API do ENEM: " + e.getMessage());
            e.printStackTrace();
        }
    }
}