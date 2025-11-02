package com.meetus.MeetUSInterview.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSearchRequest {

    @Schema(description = "Filter by task status", example = "open", allowableValues = {"open", "done"})
    private String status;
    
    @Schema(description = "Search keyword in title and description", example = "documentation")
    private String search;
    
    @Schema(description = "Filter tasks created after this date", example = "2024-11-01T00:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fromDate;
    
    @Schema(description = "Filter tasks created before this date", example = "2024-11-30T23:59:59")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime toDate;

    @Schema(description = "Page number (0-indexed)", example = "0")
    private Integer page;
    
    @Schema(description = "Page size", example = "10")
    private Integer size;

    @Schema(description = "Sort by field", example = "createdAt", allowableValues = {"id", "title", "status", "createdAt", "updatedAt"})
    private String sortBy;
    
    @Schema(description = "Sort direction", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortDirection;

    @Schema(hidden = true)
    public int getPageOrDefault() {
        return (page != null && page >= 0) ? page : 0;
    }

    @Schema(hidden = true)
    public int getSizeOrDefault() {
        return (size != null && size > 0) ? size : 10;
    }

    @Schema(hidden = true)
    public String getSortByOrDefault() {
        return (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";
    }

    @Schema(hidden = true)
    public String getSortDirectionOrDefault() {
        return (sortDirection != null && !sortDirection.isBlank()) ? sortDirection : "DESC";
    }
}
