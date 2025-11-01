package com.myhealth.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Request to change task status")
public class UserTaskStatusChangeRequest {
    
    @NotNull(message = "Status is required")
    @Pattern(regexp = "^(1|2|3|4|5)$", message = "Status must be between 1 and 5 (1=Backlog, 2=Todo, 3=In Progress, 4=Blocked, 5=Done)")
    @Schema(description = "New task status (1=Backlog, 2=Todo, 3=In Progress, 4=Blocked, 5=Done)", example = "5")
    private String statusId;
}