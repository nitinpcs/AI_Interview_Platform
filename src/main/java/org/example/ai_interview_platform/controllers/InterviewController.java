package org.example.ai_interview_platform.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ai_interview_platform.models.InterviewModel;
import org.example.ai_interview_platform.services.InterviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/start")
    public InterviewModel start(@RequestParam String domain) {
        return interviewService.startInterview(domain);
    }

    @PostMapping("/answer")
    public InterviewModel answer(@RequestParam String interviewId,
                            @RequestParam String answer) {
        return interviewService.answerQuestion(interviewId, answer);
    }

    @GetMapping("/history")
    public List<InterviewModel> history() {
        return interviewService.getHistory();
    }
}