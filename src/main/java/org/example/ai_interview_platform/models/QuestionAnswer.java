package org.example.ai_interview_platform.models;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswer {

    private String question;
    private String answer;

    private String hint;

    private String feedback;
    private int score;

    private LocalDateTime questionStartTime;
    private int timeTaken;
}