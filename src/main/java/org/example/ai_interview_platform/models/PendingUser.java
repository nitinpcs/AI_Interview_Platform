package org.example.ai_interview_platform.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "pending_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingUser {

    @Id
    private String id;

    private String name;
    private String email;
    private String password;

    private String otp;
    private LocalDateTime otpExpiry;

    private LocalDateTime createdAt;
}
