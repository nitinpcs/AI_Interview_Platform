package org.example.ai_interview_platform.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ai_interview_platform.dto.*;
import org.example.ai_interview_platform.services.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    // REGISTER
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // VERIFY OTP
    @PostMapping("/verify-otp")
    public String verify(@RequestParam String email, @RequestParam String otp) {
        return authService.verifyOtp(email, otp);
    }

    // LOGIN
    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public String forgot(@RequestParam String email) {
        return authService.forgotPassword(email);
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public String reset(@RequestParam String email,
                        @RequestParam String otp,
                        @RequestParam String newPassword) {
        return authService.resetPassword(email, otp, newPassword);
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String userId) {
        return authService.logout(userId);
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email,
                            @RequestParam String purpose) {
        otpService.sendOtp(email, purpose);
        return "OTP resent successfully";
    }
}