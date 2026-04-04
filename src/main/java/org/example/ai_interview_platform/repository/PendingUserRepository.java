package org.example.ai_interview_platform.repository;

import org.example.ai_interview_platform.models.PendingUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PendingUserRepository extends MongoRepository<PendingUser, String> {
    Optional<PendingUser> findByEmail(String email);
}
