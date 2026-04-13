package org.example.ai_interview_platform.repository;

import org.example.ai_interview_platform.models.InterviewModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends MongoRepository<InterviewModel, String> {

    // 🔹 Get all interviews of a specific user
    List<InterviewModel> findByUserEmail(String userEmail);

    // 🔹 Get only completed interviews
    List<InterviewModel> findByUserEmailAndCompletedTrue(String userEmail);

    // 🔹 Get only ongoing interviews
    List<InterviewModel> findByUserEmailAndCompletedFalse(String userEmail);

    // 🔹 Admin use: get all completed interviews
    List<InterviewModel> findByCompletedTrue();

    // 🔹 Admin use: get all ongoing interviews
    List<InterviewModel> findByCompletedFalse();

    // 🔹 Optional: filter by domain (DSA, Backend, etc.)
    List<InterviewModel> findByUserEmailAndDomain(String userEmail, String domain);
}