package org.example.ai_interview_platform.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "interviews")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewModel {

    @Id
    private String id;

    private String userEmail;
    private String domain;

    private List<QuestionAnswer> qaList;

    private int currentQuestionIndex;
    private boolean completed;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime expiresAt;

    private double averageScore;
    private String finalFeedback;

    private List<String> strongAreas;
    private List<String> weakAreas;

    private List<String> suggestions;
}