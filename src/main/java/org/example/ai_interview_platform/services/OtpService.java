package org.example.ai_interview_platform.services;

import lombok.RequiredArgsConstructor;
import org.example.ai_interview_platform.models.OtpVerification;
import org.example.ai_interview_platform.repository.OtpVerificationRepository;
import org.example.ai_interview_platform.utils.OtpUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpVerificationRepository otpRepo;
    private final EmailService emailService;

    private static final int MAX_REQUESTS = 3;
    private static final int WINDOW_MINUTES = 5;
    private static final int COOLDOWN_SECONDS = 30;


    public void sendOtp(String email, String purpose) {

        LocalDateTime now = LocalDateTime.now();


        long count = otpRepo.countByEmailAndPurposeAndCreatedAtAfter(
                email,
                purpose,
                now.minusMinutes(WINDOW_MINUTES)
        );

        if (count >= MAX_REQUESTS) {
            throw new RuntimeException("Too many OTP requests. Try again later.");
        }


        otpRepo.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .ifPresent(lastOtp -> {
                    if (lastOtp.getCreatedAt().plusSeconds(COOLDOWN_SECONDS).isAfter(now)) {
                        throw new RuntimeException("Please wait before requesting another OTP.");
                    }
                });

        String otp = OtpUtil.generateOtp();

        OtpVerification otpEntity = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .purpose(purpose)
                .createdAt(now)
                .expiryTime(now.plusMinutes(5))
                .used(false)
                .build();

        otpRepo.save(otpEntity);

        emailService.sendOtp(email, otp);
    }


    public void verifyOtp(String email, String otp, String purpose) {

        OtpVerification entity = otpRepo
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (entity.isUsed()) {
            throw new RuntimeException("OTP already used");
        }

        if (!entity.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (entity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        entity.setUsed(true);
        otpRepo.save(entity);
    }
}