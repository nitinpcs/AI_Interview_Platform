package org.example.ai_interview_platform.services;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.ai_interview_platform.models.UserModel;
import org.example.ai_interview_platform.repository.UserRepository;
import org.example.ai_interview_platform.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final UserRepository userRepository;

    // 🔹 Upload + Parse Resume
    public String uploadResume(MultipartFile file) {

        try {
            InputStream inputStream = file.getInputStream();

            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper pdfStripper = new PDFTextStripper();

            String text = pdfStripper.getText(document);
            document.close();

            String email = SecurityUtils.getCurrentUserEmail();

            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setResumeText(text);

            // 🔥 Extract skills (basic version)
            List<String> skills = extractSkills(text);
            user.setSkills(skills);

            userRepository.save(user);

            return "Resume uploaded & parsed successfully";

        } catch (Exception e) {
            throw new RuntimeException("Error parsing resume: " + e.getMessage());
        }
    }

    // 🔹 Basic skill extraction
    private List<String> extractSkills(String text) {

        List<String> knownSkills = Arrays.asList(
                "java", "spring", "spring boot", "react", "node",
                "mongodb", "mysql", "docker", "kubernetes",
                "aws", "dsa", "system design"
        );

        String lowerText = text.toLowerCase();

        return knownSkills.stream()
                .filter(lowerText::contains)
                .collect(Collectors.toList());
    }
}
