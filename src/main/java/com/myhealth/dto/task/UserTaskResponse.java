package com.myhealth.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Schema(description = "User task response")
public class UserTaskResponse {
    
    @Schema(description = "Task ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Task title", example = "Complete project documentation")
    private String title;
    
    @Schema(description = "Task description in markdown", example = "Write comprehensive documentation for the API")
    private String descriptionMd;
    
    @Schema(description = "Task status ID", example = "2")
    private Short statusId;
    
    @Schema(description = "Task status name", example = "Todo")
    private String statusName;
    
    @Schema(description = "Task priority ID", example = "3")
    private Short priorityId;
    
    @Schema(description = "Task priority name", example = "Medium")
    private String priorityName;
    
    @Schema(description = "Task due date", example = "2024-12-31T23:59:59Z")
    private ZonedDateTime dueAt;
    
    @Schema(description = "Estimated time in minutes", example = "120")
    private Integer estimateMinutes;
    
    @Schema(description = "Time spent in minutes", example = "60")
    private Integer spentMinutes;
    
    @Schema(description = "Task completion date", example = "2024-01-15T10:30:00Z")
    private ZonedDateTime completedAt;
    
    @Schema(description = "Task creation date", example = "2024-01-01T09:00:00Z")
    private ZonedDateTime createdAt;
    
    @Schema(description = "Task last update date", example = "2024-01-10T14:30:00Z")
    private ZonedDateTime updatedAt;
}