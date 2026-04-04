package org.example.ai_interview_platform.services;

import lombok.RequiredArgsConstructor;
import org.example.ai_interview_platform.dto.*;
import org.example.ai_interview_platform.models.*;
import org.example.ai_interview_platform.repository.*;
import org.example.ai_interview_platform.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PendingUserRepository pendingUserRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 🔹 REGISTER (send OTP)
    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "User already exists";
        }

        PendingUser pending = PendingUser.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        pendingUserRepository.save(pending);

        otpService.sendOtp(request.getEmail(), "REGISTER");

        return "OTP sent to email";
    }

    // 🔹 VERIFY OTP (registration)
    public String verifyOtp(String email, String otp) {

        otpService.verifyOtp(email, otp, "REGISTER");

        PendingUser pending = pendingUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserModel user = UserModel.builder()
                .name(pending.getName())
                .email(pending.getEmail())
                .password(pending.getPassword())
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        pendingUserRepository.delete(pending);

        return "User verified successfully";
    }


    // 🔹 LOGIN
    public AuthResponseDto login(LoginRequest request) {

        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getRole().name()
        );
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        refreshTokenRepository.deleteByUserId(user.getId());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .expiryDate(LocalDateTime.now().plusDays(7))
                        .build()
        );

        return new AuthResponseDto(accessToken, refreshToken);
    }

    // 🔹 FORGOT PASSWORD (send OTP)
    public String forgotPassword(String email) {

        if (!userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User not found");
        }

        otpService.sendOtp(email, "RESET_PASSWORD");

        return "OTP sent for password reset";
    }

    // 🔹 RESET PASSWORD
    public String resetPassword(String email, String otp, String newPassword) {

        otpService.verifyOtp(email, otp, "RESET_PASSWORD");

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password updated successfully";
    }

    // 🔹 REFRESH TOKEN
    public String refresh(String refreshToken) {

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        String email = jwtUtil.extractEmail(refreshToken);

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getRole().name()
        );
    }

    // 🔹 LOGOUT
    public String logout(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        return "Logged out";
    }
}