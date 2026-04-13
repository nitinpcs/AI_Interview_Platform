package org.example.ai_interview_platform.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.ai_interview_platform.dto.AIReportResponse;
import org.example.ai_interview_platform.models.*;
import org.example.ai_interview_platform.repository.InterviewRepository;
import org.example.ai_interview_platform.repository.UserRepository;
import org.example.ai_interview_platform.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final QuestionService questionService;
    private final UserRepository userRepository;
    private final AIService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 🔹 START INTERVIEW
    public InterviewModel startInterview(String domain) {

        String email = SecurityUtils.getCurrentUserEmail();

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> skills = user.getSkills();
        if (skills == null || skills.isEmpty()) skills = List.of("general");

        String firstQuestion = questionService.getQuestion(skills, 0);

        InterviewModel interview = InterviewModel.builder()
                .userEmail(email)
                .domain(domain)
                .qaList(new ArrayList<>())
                .currentQuestionIndex(0)
                .completed(false)
                .startedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(20))
                .build();

        interview.getQaList().add(
                QuestionAnswer.builder()
                        .question(firstQuestion)
                        .questionStartTime(LocalDateTime.now())
                        .build()
        );

        return interviewRepository.save(interview);
    }

    // 🔹 ANSWER QUESTION
    public InterviewModel answerQuestion(String interviewId, String answer) {

        InterviewModel interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        // ⏱ Check interview expiry
        if (LocalDateTime.now().isAfter(interview.getExpiresAt())) {
            interview.setCompleted(true);
            interview.setCompletedAt(LocalDateTime.now());
            generateFinalReport(interview);
            return interviewRepository.save(interview);
        }

        int index = interview.getCurrentQuestionIndex();
        QuestionAnswer qa = interview.getQaList().get(index);

        qa.setAnswer(answer);

        // ⏱ Time tracking
        long seconds = Duration.between(
                qa.getQuestionStartTime(),
                LocalDateTime.now()
        ).getSeconds();

        qa.setTimeTaken((int) seconds);

        // 🔥 LIGHTWEIGHT HINT (only 1 line)
        qa.setHint(aiService.generateHint(qa.getQuestion(), answer));

        index++;

        UserModel user = userRepository.findByEmail(interview.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> skills = user.getSkills();
        if (skills == null || skills.isEmpty()) skills = List.of("general");

        String nextQuestion = questionService.getQuestion(skills, index);

        if (nextQuestion == null || index >= 5) {
            interview.setCompleted(true);
            interview.setCompletedAt(LocalDateTime.now());
            generateFinalReport(interview);
        } else {
            interview.getQaList().add(
                    QuestionAnswer.builder()
                            .question(nextQuestion)
                            .questionStartTime(LocalDateTime.now())
                            .build()
            );
            interview.setCurrentQuestionIndex(index);
        }

        return interviewRepository.save(interview);
    }

    // 🔹 FINAL REPORT (AI evaluates entire interview)
    private void generateFinalReport(InterviewModel interview) {

        StringBuilder transcript = new StringBuilder();

        for (QuestionAnswer qa : interview.getQaList()) {
            transcript.append("Q: ").append(qa.getQuestion()).append("\n");
            transcript.append("A: ").append(qa.getAnswer()).append("\n\n");
        }

        String aiResponse = aiService.generateFinalReport(transcript.toString());

        try {
            AIReportResponse report =
                    objectMapper.readValue(aiResponse, AIReportResponse.class);

            // ✅ Store parsed values
            interview.setAverageScore(report.getScore());
            interview.setStrongAreas(report.getStrengths());
            interview.setWeakAreas(report.getWeaknesses());

            // ✅ ADD THIS LINE HERE
            interview.setSuggestions(report.getSuggestions());

            // Optional: store full JSON for UI/debug
            interview.setFinalFeedback(aiResponse);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        }
    }
    // 🔹 Extract score
    private double extractScore(String response) {
        try {
            return Double.parseDouble(response.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 5;
        }
    }

    // 🔹 Extract list (basic parsing)
    private List<String> extractList(String response, String key) {
        return List.of(key); // placeholder (can improve later)
    }

    // 🔹 HISTORY
    public List<InterviewModel> getHistory() {
        String email = SecurityUtils.getCurrentUserEmail();
        return interviewRepository.findByUserEmail(email);
    }
}