package org.example.ai_interview_platform.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "otp_verifications")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerification {

    @Id
    private String id;

    private String email;
    private String otp;
    private String purpose;

    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;
    private boolean used;
}