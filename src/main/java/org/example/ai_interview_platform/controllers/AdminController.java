package org.example.ai_interview_platform.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String admin() {
        return "Admin access granted";
    }
}
