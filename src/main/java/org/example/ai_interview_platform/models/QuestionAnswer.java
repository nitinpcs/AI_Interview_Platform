package org.example.ai_interview_platform.models;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswer {

    private String question;
    private String answer;
    private String feedback;
    private int score;
}