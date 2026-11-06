package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.VideoConsumo;
import com.estudamais.backend.repository.VideoConsumoRepository;
import com.estudamais.backend.service.VideoConsumoService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumoServiceImpl implements VideoConsumoService {

    private static final Pattern YOUTUBE_ID = Pattern.compile(
            "(?:youtu\\.be/|[?&]v=|/embed/|/shorts/)([A-Za-z0-9_-]{11})");

    private final VideoConsumoRepository videoConsumoRepository;
    private final RestClient restClient = RestClient.create();

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    public ResumoServiceImpl(VideoConsumoRepository videoConsumoRepository) {
        this.videoConsumoRepository = videoConsumoRepository;
    }

    @Override
    public String gerarResumo(String videoUrl) {
        String videoId = extrairVideoId(videoUrl);

        Optional<VideoConsumo> existente = videoConsumoRepository.findByVideoId(videoId);
        if (existente.isPresent() && existente.get().getConteudoResumo() != null) {
            return existente.get().getConteudoResumo();
        }

        String prompt = """
            Você é um assistente de engenharia de software focado em estudos.
            Assista à videoaula e faça um resumo bem estruturado em português:
            - Inclua um título.
            - Um parágrafo de introdução.
            - Os conceitos principais em bullet points.
            Use formatação Markdown.
            """;

        Map<String, Object> body = Map.of("contents", List.of(Map.of("parts", List.of(
                Map.of("file_data", Map.of("file_uri", "https://www.youtube.com/watch?v=" + videoId)),
                Map.of("text", prompt)
        ))));

        JsonNode resposta = restClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent")
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        String resumo = resposta.path("candidates").path(0)
                .path("content").path("parts").path(0).path("text").asText(null);

        if (resumo == null || resumo.isBlank()) {
            throw new IllegalStateException("O Gemini não retornou um resumo.");
        }

        VideoConsumo novo = videoConsumoRepository.findByVideoId(videoId).orElseGet(VideoConsumo::new);
        novo.setVideoId(videoId);
        novo.setConteudoResumo(resumo);
        videoConsumoRepository.save(novo);

        return resumo;
    }

    private String extrairVideoId(String url) {
        if (url != null) {
            Matcher m = YOUTUBE_ID.matcher(url);
            if (m.find()) return m.group(1);
        }
        throw new IllegalArgumentException("URL de vídeo inválida: " + url);
    }
}

