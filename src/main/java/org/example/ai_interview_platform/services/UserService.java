package org.example.ai_interview_platform.services;

import lombok.RequiredArgsConstructor;
import org.example.ai_interview_platform.models.UserModel;
import org.example.ai_interview_platform.repository.UserRepository;
import org.example.ai_interview_platform.utils.SecurityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 🔹 GET PROFILE
    public UserModel getProfile() {

        String email = SecurityUtils.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔹 UPDATE PROFILE
    public String updateProfile(String name) {

        String email = SecurityUtils.getCurrentUserEmail();

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(name);

        userRepository.save(user);

        return "Profile updated successfully";
    }

    // 🔹 CHANGE PASSWORD
    public String changePassword(String oldPassword, String newPassword) {

        String email = SecurityUtils.getCurrentUserEmail();

        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password changed successfully";
    }
}
