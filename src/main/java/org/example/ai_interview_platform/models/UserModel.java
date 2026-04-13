
package org.example.ai_interview_platform.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {

    @Id
    private String id;

    private String name;
    private String email;
    private String password;
    private Role role;

    private String resumeUrl;

    private String resumeText;
    private List<String> skills;
    private LocalDateTime createdAt;
}