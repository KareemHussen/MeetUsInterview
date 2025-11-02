package com.meetus.MeetUSInterview.dto.response.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    @Schema(description = "Task ID", example = "1")
    private Long id;
    
    @Schema(description = "Task title", example = "Complete project documentation")
    private String title;
    
    @Schema(description = "Task description", example = "Write comprehensive API documentation with examples and usage guidelines")
    private String description;
    
    @Schema(description = "Task status", example = "open")
    private String status;
    
    @Schema(description = "User ID who owns this task", example = "1")
    private Long userId;
    
    @Schema(description = "Task creation timestamp", example = "2024-11-02T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Task last update timestamp", example = "2024-11-02T14:30:00")
    private LocalDateTime updatedAt;
}
