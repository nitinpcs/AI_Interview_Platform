package org.example.ai_interview_platform.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ai_interview_platform.services.ResumeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    // 🔹 Upload Resume
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) {
        return resumeService.uploadResume(file);
    }
}