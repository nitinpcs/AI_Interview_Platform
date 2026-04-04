

package org.example.ai_interview_platform.repository;

import org.example.ai_interview_platform.models.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserModel, String> {

    Optional<UserModel> findByEmail(String email);
}