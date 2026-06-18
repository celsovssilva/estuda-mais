package com.estudamais.backend.service.serviceimpl;
import com.estudamais.backend.entity.ReviewMaterial;
import com.estudamais.backend.repository.ReviewMaterialRepository;
import com.estudamais.backend.request.ReviewImportRequest;
import com.estudamais.backend.response.ReviewResponse;
import com.estudamais.backend.service.AiIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiIntegrationServiceImpl implements AiIntegrationService {

    private final ReviewMaterialRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.url}")
    private String geminiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Override
    public ReviewResponse importAndProcessMaterial(Long userId, ReviewImportRequest request) {
        String summaryPrompt = "Faça um resumo didático, estruturado em tópicos, focado em estudantes para o seguinte conteúdo: " + request.getRawContent();
        String summary = callGemini(summaryPrompt);

        String quizPrompt = "Com base no texto a seguir, gere um simulado/prova com 3 questões de múltipla escolha. Retorne estritamente um array JSON válido contendo objetos com 'pergunta', 'opcoes' (array de strings) e 'respostaCorreta'. Texto: " + request.getRawContent();
        String quiz = callGemini(quizPrompt);

        ReviewMaterial material = ReviewMaterial.builder()
                .userId(userId)
                .title(request.getTitle())
                .rawContent(request.getRawContent())
                .aiSummary(summary)
                .aiGeneratedQuiz(quiz)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(material);

        return convertToResponse(material);
    }

    @Override
    public List<ReviewResponse> getUserReviews(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String exportReviewText(Long userId, Long reviewId) {
        ReviewMaterial material = repository.findById(reviewId)
                .filter(m -> m.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Material de revisão não localizado ou acesso negado."));

        return "=== " + material.getTitle() + " ===\n\n" +
                "RESUMO DA IA:\n" + material.getAiSummary() + "\n\n" +
                "PROVA DA REVISÃO:\n" + material.getAiGeneratedQuiz();
    }

    private String callGemini(String promptText) {
        try {
            String url = geminiUrl + "?key=" + geminiApiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Estrutura do payload exigido pela API do Gemini
            Map<String, Object> textMap = Map.of("text", promptText);
            Map<String, Object> partsMap = Map.of("parts", List.of(textMap));
            Map<String, Object> contentsMap = Map.of("contents", List.of(partsMap));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contentsMap, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);


            List candidates = (List) response.getBody().get("candidates");
            Map candidate = (Map) candidates.get(0);
            Map content = (Map) candidate.get("content");
            List parts = (List) content.get("parts");
            Map part = (Map) parts.get(0);

            return (String) part.get("text");
        } catch (Exception e) {
            return "Falha ao processar requisição com a IA. Erro: " + e.getMessage();
        }
    }

    private ReviewResponse convertToResponse(ReviewMaterial m) {
        return ReviewResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .aiSummary(m.getAiSummary())
                .aiGeneratedQuiz(m.getAiGeneratedQuiz())
                .createdAt(m.getCreatedAt())
                .build();
    }
}