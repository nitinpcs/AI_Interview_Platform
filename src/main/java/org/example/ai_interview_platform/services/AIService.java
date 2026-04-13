package org.example.ai_interview_platform.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final WebClient.Builder webClientBuilder;

    @Value("${ai.api.key}")
    private String apiKey;

    // 🔹 Evaluate Answer
    public Map<String, Object> evaluateAnswer(String question, String answer) {

        String prompt = """
                You are an interviewer.

                Question: %s
                Answer: %s

                Give response strictly in format:
                Score: X/10
                Feedback: ...
                """.formatted(question, answer);

        String response = callOpenAI(prompt);

        int score = extractScore(response);

        return Map.of(
                "feedback", response,
                "score", score
        );
    }

    // 🔹 Generate Question
    public String generateQuestion(String skill) {
        String prompt = "Generate one technical interview question on " + skill;
        return callOpenAI(prompt);
    }

    // 🔹 Final Summary
    public String generateFinalSummary(String data) {
        String prompt = "Summarize interview performance:\n" + data;
        return callOpenAI(prompt);
    }

    // 🔹 OpenAI Call
    private String callOpenAI(String prompt) {

        WebClient client = webClientBuilder.baseUrl("https://api.openai.com/v1").build();

        Map<String, Object> request = Map.of(
                "model", "gpt-4o-mini",
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                }
        );

        return client.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // 🔹 Extract score
    private int extractScore(String response) {
        try {
            String line = response.split("\n")[0];
            return Integer.parseInt(line.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 5;
        }
    }
}