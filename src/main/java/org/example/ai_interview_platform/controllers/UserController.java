package org.example.ai_interview_platform.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ai_interview_platform.models.UserModel;
import org.example.ai_interview_platform.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 🔹 GET PROFILE
    @GetMapping("/profile")
    public UserModel profile() {
        return userService.getProfile();
    }

    // 🔹 UPDATE PROFILE
    @PutMapping("/update")
    public String update(@RequestParam String name) {
        return userService.updateProfile(name);
    }

    // 🔹 CHANGE PASSWORD
    @PutMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword) {
        return userService.changePassword(oldPassword, newPassword);
    }
}