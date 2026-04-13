package org.example.ai_interview_platform.services;

import lombok.RequiredArgsConstructor;
import org.example.ai_interview_platform.models.*;
import org.example.ai_interview_platform.repository.InterviewRepository;
import org.example.ai_interview_platform.repository.UserRepository;
import org.example.ai_interview_platform.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final QuestionService questionService;
    private final UserRepository userRepository;
    private final AIService aiService;

    // 🔹 START INTERVIEW
    public InterviewModel startInterview(String domain) {

        String email = SecurityUtils.getCurrentUserEmail();

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> skills = user.getSkills();

        if (skills == null || skills.isEmpty()) {
            skills = List.of("general");
        }

        String firstQuestion = questionService.getQuestion(skills, 0);

        InterviewModel interview = InterviewModel.builder()
                .userEmail(email)
                .domain(domain)
                .qaList(new ArrayList<>())
                .currentQuestionIndex(0)
                .completed(false)
                .startedAt(LocalDateTime.now())
                .build();

        interview.getQaList().add(
                QuestionAnswer.builder()
                        .question(firstQuestion)
                        .build()
        );

        return interviewRepository.save(interview);
    }

    // 🔹 ANSWER QUESTION
    public InterviewModel answerQuestion(String interviewId, String answer) {

        InterviewModel interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        int index = interview.getCurrentQuestionIndex();

        QuestionAnswer qa = interview.getQaList().get(index);
        qa.setAnswer(answer);

        // 🔥 AI evaluation
        Map<String, Object> aiResult =
                aiService.evaluateAnswer(qa.getQuestion(), answer);

        qa.setFeedback((String) aiResult.get("feedback"));
        qa.setScore((Integer) aiResult.get("score"));

        index++;

        UserModel user = userRepository.findByEmail(interview.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> skills = user.getSkills();

        if (skills == null || skills.isEmpty()) {
            skills = List.of("general");
        }

        String nextQuestion = questionService.getQuestion(skills, index);

        if (nextQuestion == null || index >= 5) {
            interview.setCompleted(true);
            interview.setCompletedAt(LocalDateTime.now());
            generateFinalReport(interview);
        } else {
            interview.getQaList().add(
                    QuestionAnswer.builder()
                            .question(nextQuestion)
                            .build()
            );
            interview.setCurrentQuestionIndex(index);
        }

        return interviewRepository.save(interview);
    }

    // 🔹 FINAL REPORT
    private void generateFinalReport(InterviewModel interview) {

        double avg = interview.getQaList().stream()
                .mapToInt(QuestionAnswer::getScore)
                .average()
                .orElse(0);

        interview.setAverageScore(avg);

        interview.setWeakAreas(
                interview.getQaList().stream()
                        .filter(q -> q.getScore() < 5)
                        .map(q -> q.getQuestion())
                        .distinct()
                        .toList()
        );

        String summary = aiService.generateFinalSummary(interview.toString());

        interview.setFinalFeedback(summary);
    }

    // 🔹 HISTORY
    public List<InterviewModel> getHistory() {
        String email = SecurityUtils.getCurrentUserEmail();
        return interviewRepository.findByUserEmail(email);
    }
}