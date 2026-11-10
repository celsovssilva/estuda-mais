package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.VideoConsumo;
import com.estudamais.backend.repository.VideoConsumoRepository;
import com.estudamais.backend.service.VideoConsumoService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumoServiceImpl implements VideoConsumoService {

    private static final Pattern YOUTUBE_ID = Pattern.compile(
            "(?:youtu\\.be/|[?&]v=|/embed/|/shorts/)([A-Za-z0-9_-]{11})");

    private final VideoConsumoRepository videoConsumoRepository;
    private final ChatClient chatClient;


    public ResumoServiceImpl(VideoConsumoRepository videoConsumoRepository, ChatClient.Builder builder) {
        this.videoConsumoRepository = videoConsumoRepository;
        this.chatClient = builder.build();
    }

    @Override
    public String gerarResumo(String videoUrl) {
        String videoId = extrairVideoId(videoUrl);

        Optional<VideoConsumo> existente = videoConsumoRepository.findByVideoId(videoId);
        if (existente.isPresent() && existente.get().getConteudoResumo() != null) {
            return existente.get().getConteudoResumo();
        }
        String transcricao = "Nesta videoaula, vamos abordar os conceitos fundamentais do Java, a criação de APIs REST com Spring Boot, e como o padrão Model-View-Controller ajuda a organizar a arquitetura de software para sistemas escaláveis.";

        String prompt = "Você é um assistente de engenharia de software focado em estudos. " +
                "Baseado na transcrição desta videoaula, faça um resumo bem estruturado em português: \n" +
                "- Inclua um título.\n" +
                "- Um parágrafo de introdução.\n" +
                "- Os conceitos principais em bullet points.\n" +
                "Use formatação Markdown.\n\n" +
                "Transcrição da aula:\n" + transcricao;


        String resumo = chatClient.prompt(prompt).call().content();

        if (resumo == null || resumo.isBlank()) {
            throw new IllegalStateException("O Gemini não retornou um resumo.");
        }

        VideoConsumo novo = existente.orElseGet(VideoConsumo::new);
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