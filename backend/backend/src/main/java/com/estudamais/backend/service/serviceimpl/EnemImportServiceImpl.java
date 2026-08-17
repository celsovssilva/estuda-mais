package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.DiaProva;
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

        int offset = 0;
        int limit = 50;
        boolean temMaisPaginas = true;

        while (temMaisPaginas) {
            JsonNode root = restClient.get()
                    .uri("https://api.enem.dev/v1/exams/{year}/questions?offset={offset}&limit={limit}", ano, offset, limit)
                    .retrieve()
                    .body(JsonNode.class);


            if (root == null || !root.has("questions")) {
                break;
            }

            JsonNode questionsArray = root.get("questions");

            if (questionsArray.isEmpty()) {
                break;
            }

            List<Question> questoesDaPagina = new ArrayList<>();

            for (JsonNode node : questionsArray) {

                Question q = new Question();

                int index = node.path("index").asInt();
                String idioma = node.path("language").isNull() ? null : node.path("language").asText();
                String sufixoIdioma = (idioma != null) ? "-" + idioma : "";
                String idString = ano + "-" + index + sufixoIdioma;


                String disciplina = node.path("discipline").asText("geral");

                DiaProva dataprova;

                if ("linguagens".equals(disciplina) || "ciencias-humanas".equals(disciplina)) {
                    dataprova = DiaProva.DIA_1;
                } else {
                    dataprova = DiaProva.DIA_2;
                }


               StringBuilder enunciado = new StringBuilder();
                String context = node.path("context").asText("");

                String contexto = context.replaceAll("!\\[.*?\\]\\((.*?)\\)", "<br><img src=\"$1\" style=\"max-width: 100%; border-radius: 8px;\" /><br>");
                enunciado.append(contexto);

                String questionamento = node.path("alternativesIntroduction").asText("");
                if(!questionamento.isBlank()){
                    enunciado.append("<br><br><strong>").append(questionamento).append("</strong>");
                }

                JsonNode alternativas = node.path("alternatives");
                List<String> listaAlternativas = new ArrayList<>();

                if (alternativas.isArray()) {
                    for (JsonNode altNode : alternativas) {
                        String textoAlt = altNode.path("text").isNull() ? null : altNode.path("text").asText();
                        String arquivoAlt = altNode.path("file").isNull() ? null : altNode.path("file").asText();

                        String valorFinal;
                        if (textoAlt != null && !textoAlt.isBlank()) {
                            valorFinal = textoAlt;
                        } else if (arquivoAlt != null && !arquivoAlt.isBlank()) {
                            valorFinal = "<img src=\"" + arquivoAlt + "\" />";
                        } else {
                            valorFinal = "";
                        }

                        listaAlternativas.add(valorFinal);

                        Boolean correta = altNode.path("isCorrect").asBoolean(false);
                        if (correta) {
                            q.setRespostaCorreta(valorFinal);
                        }
                    }
                }


                q.setId(idString);
                q.setAno(ano);
                q.setNumero(    index);
                q.setIdioma(idioma);
                q.setDisciplina(disciplina);
                q.setDia(dataprova);
                q.setEnunciado(enunciado.toString());
                q.setAlternativas(listaAlternativas);


                questoesDaPagina.add(q);
            }


            questaoRepository.saveAll(questoesDaPagina);

            offset += limit;
        }
    }
    }
