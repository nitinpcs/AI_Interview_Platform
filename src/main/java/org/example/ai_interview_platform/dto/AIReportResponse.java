package org.example.ai_interview_platform.dto;

import lombok.Data;

import java.util.List;

@Data
public class AIReportResponse {

    private int score;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> suggestions;
}
