package org.example.ai_interview_platform.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final WebClient.Builder webClientBuilder;

    @Value("${ai.api.key}")
    private String apiKey;

    // 🔹 Generate Question
    public String generateQuestion(String skill) {
        String prompt = "Generate one technical interview question on " + skill;
        return callOpenAI(prompt);
    }

    // 🔹 Generate Hint
    public String generateHint(String question, String answer) {

        String prompt = """
                Give a very short hint (1 line) to improve this answer.

                Question: %s
                Answer: %s
                """.formatted(question, answer);

        return callOpenAI(prompt);
    }

    // 🔹 Final Report
    public String generateFinalReport(String transcript) {

        String prompt = """
                You are an expert interviewer.

                Evaluate this interview:

                %s

                Return STRICT JSON:
                {
                  "score": 7,
                  "strengths": ["..."],
                  "weaknesses": ["..."],
                  "suggestions": ["..."]
                }
                """.formatted(transcript);

        return callOpenAI(prompt);
    }

    // 🔹 CORE FIXED METHOD
    private String callOpenAI(String prompt) {

        WebClient client = webClientBuilder.baseUrl("https://api.openai.com/v1").build();

        Map<String, Object> request = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        Map<String, Object> response = client.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return extractContent(response);
    }

    // 🔹 Extract only AI message content
    private String extractContent(Map<String, Object> response) {

        try {
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.get("choices");

            Map<String, Object> firstChoice = choices.get(0);

            Map<String, Object> message =
                    (Map<String, Object>) firstChoice.get("message");

            return message.get("content").toString().trim();

        } catch (Exception e) {
            throw new RuntimeException("Error parsing OpenAI response: " + e.getMessage());
        }
    }
}