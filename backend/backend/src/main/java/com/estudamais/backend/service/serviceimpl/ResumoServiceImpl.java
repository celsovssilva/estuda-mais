package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.VideoConsumo;
import com.estudamais.backend.repository.VideoConsumoRepository;
import com.estudamais.backend.service.VideoConsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Optional;

@Service
public class ResumoServiceImpl implements VideoConsumoService {
    @Autowired
    private VideoConsumoRepository videoConsumoRepository;
    @Autowired
    private  ChatClient chatClient;
    @Override
    public String gerarResumo(String videoUrl) {
        String videoId = extrairVideoId(videoUrl);

        Optional<VideoConsumo> resumoExistente = videoConsumoRepository.findByVideoId(videoId);
        if (resumoExistente.isPresent()) {
            return resumoExistente.get().getConteudoResumo();
        }


        String textoDaAula = obterTranscricao(videoId);

        String instrucao = """
            Você é um assistente de engenharia de software focado em estudos.
            Faça um resumo bem estruturado da videoaula fornecida.
            - Inclua um título.
            - Faça um parágrafo de introdução.
            - Liste os conceitos principais em bullet points (marcadores).
            - Utilize formatação Markdown.
            
            Videoaula:
            """ + textoDaAula;


        String respostaGemini = chatClient.prompt(instrucao).call().content();

        VideoConsumo novoResumo = new VideoConsumo();
        novoResumo.setVideoId(videoId);
        novoResumo.setConteudoResumo(respostaGemini);
        videoConsumoRepository.save(novoResumo);

        return respostaGemini;
    }
    private String extrairVideoId(String url) {
        // Lógica simples para pegar o ID da URL do YouTube
        if (url != null && url.contains("v=")) {
            return url.split("v=")[1].split("&")[0];
        }
        return "ID_DESCONHECIDO";
    }

    private String obterTranscricao(String videoId) {

        return "Olá pessoal, nesta aula vamos aprender sobre Java, Spring Boot e como integrar inteligência artificial utilizando a arquitetura Model-View-Controller.";
    }
}
