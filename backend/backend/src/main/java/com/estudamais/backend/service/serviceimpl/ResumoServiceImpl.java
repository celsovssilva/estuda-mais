package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.VideoConsumo;
import com.estudamais.backend.repository.VideoConsumoRepository;
import com.estudamais.backend.service.VideoConsumoService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumoServiceImpl implements VideoConsumoService {
    // metodo privado para conferir se o link é mesmo do youtube através do regex
    private static final Pattern YOUTUBE_ID = Pattern.compile(
            "(?:youtu\\.be/|[?&]v=|/embed/|/shorts/)([A-Za-z0-9_-]{11})");
    //prompt
    private static final String PROMPT =
            "Você é um assistente de engenharia de software focado em estudos. " +
                    "Assista a esta videoaula e faça um resumo bem estruturado em português: " +
                    "um título, um parágrafo de introdução e os conceitos principais em bullet points. " +
                    "Use formatação Markdown.";

    private final VideoConsumoRepository videoConsumoRepository;
    private final RestClient restClient = RestClient.create();

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.chat.options.model}")
    private String modelo;

    public ResumoServiceImpl(VideoConsumoRepository videoConsumoRepository) {
        this.videoConsumoRepository = videoConsumoRepository;
    }

    @Override
    public String gerarResumo(String videoUrl) {
        //chama o metodo privado extrair o video e enviando para o google
        String videoId = extrairVideoId(videoUrl);
        // verifica se já existe no banco um resumo para esse video, se sim, trás ele
        Optional<VideoConsumo> existente = videoConsumoRepository.findByVideoId(videoId);
        if (existente.isPresent() && existente.get().getConteudoResumo() != null) {
            return existente.get().getConteudoResumo();
        }

        //resume o video
        String resumo = resumirVideo(videoId);

        if (resumo == null || resumo.isBlank()) {
            throw new IllegalStateException("O Gemini não retornou um resumo.");
        }
        //salva o resumo no banco, se outro user for resumir um mesmo video, ele traz do bannco
        // para a aplicação ser mais rapida
        VideoConsumo novo = existente.orElseGet(VideoConsumo::new);
        novo.setVideoId(videoId);
        novo.setConteudoResumo(resumo);
        videoConsumoRepository.save(novo);

        return resumo;
    }

    private String resumirVideo(String videoId) {
        //ele monta o pedido, ele lê o link e passa o prompt
        Map<String, Object> body = Map.of("contents", List.of(Map.of("parts", List.of(
                Map.of("file_data", Map.of("file_uri", "https://www.youtube.com/watch?v=" + videoId)),
                Map.of("text", PROMPT)))));
        //consome a api, verifica a chave, o tipo de dados(json)
        JsonNode resposta = restClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/{m}:generateContent", modelo)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        //devolve a resposta convertida em texto
        return resposta.path("candidates").path(0).path("content")
                .path("parts").path(0).path("text").asText(null);
    }
    //extrai video do youtube
    private String extrairVideoId(String url) {
        if (url != null) {
            Matcher m = YOUTUBE_ID.matcher(url);
            if (m.find()) return m.group(1);
        }
        throw new IllegalArgumentException("URL de vídeo inválida: " + url);
    }
}