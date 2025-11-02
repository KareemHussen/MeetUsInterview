package com.meetus.MeetUSInterview.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user response (without password)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;
    
    @Schema(description = "User's full name", example = "Kareem Hussen")
    private String name;
    
    @Schema(description = "User's email address", example = "kareemhussen500@gmail.com")
    private String email;
    
    @Schema(description = "Account creation timestamp", example = "2024-11-01T10:00:00")
    private LocalDateTime createdAt;
}
