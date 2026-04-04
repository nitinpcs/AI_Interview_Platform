package org.example.ai_interview_platform.repository;

import org.example.ai_interview_platform.models.OtpVerification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpVerificationRepository extends MongoRepository<OtpVerification, String> {

    Optional<OtpVerification> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, String purpose);

    long countByEmailAndPurposeAndCreatedAtAfter(String email, String purpose, LocalDateTime time);
}