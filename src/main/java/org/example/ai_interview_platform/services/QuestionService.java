package org.example.ai_interview_platform.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final AIService aiService;

    public String getQuestion(List<String> skills, int index) {

        if (skills == null || skills.isEmpty()) {
            return "Tell me about yourself";
        }

        String skill = skills.get(index % skills.size());

        return aiService.generateQuestion(skill);
    }
}