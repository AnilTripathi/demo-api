package com.myhealth.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Schema(description = "Request to update an existing user task")
public class UserTaskUpdateRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Task title", example = "Complete project documentation")
    private String title;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(description = "Task description in markdown", example = "Write comprehensive documentation for the API")
    private String descriptionMd;
    
    @Pattern(regexp = "^(1|2|3|4|5)$", message = "Priority must be between 1 and 5")
    @Schema(description = "Task priority (1=Lowest, 5=Highest)", example = "3")
    private String priorityId;
    
    @Schema(description = "Task due date", example = "2024-12-31T23:59:59Z")
    private ZonedDateTime dueAt;
    
    @Schema(description = "Estimated time in minutes", example = "120")
    private Integer estimateMinutes;
}