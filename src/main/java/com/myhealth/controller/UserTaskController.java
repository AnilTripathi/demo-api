package com.myhealth.controller;

import com.myhealth.dto.task.UserTaskCreateRequest;
import com.myhealth.dto.task.UserTaskResponse;
import com.myhealth.dto.task.UserTaskStatusChangeRequest;
import com.myhealth.dto.task.UserTaskUpdateRequest;
import com.myhealth.service.UserTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/task")
@RequiredArgsConstructor
@Tag(name = "User Tasks", description = "User task management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserTaskController {
    
    private final UserTaskService userTaskService;
    
    @Operation(summary = "List user tasks", description = "Get paginated list of user tasks with optional filters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class)))
    })
    @GetMapping
    public ResponseEntity<Page<UserTaskResponse>> getUserTasks(
            @Parameter(description = "Filter by status ID (1=Backlog, 2=Todo, 3=In Progress, 4=Blocked, 5=Done)")
            @RequestParam(required = false) Short status,
            @Parameter(description = "Search query for title and description")
            @RequestParam(required = false) String q,
            @Parameter(description = "Filter tasks due from this date")
            @RequestParam(required = false) ZonedDateTime fromDue,
            @Parameter(description = "Filter tasks due until this date")
            @RequestParam(required = false) ZonedDateTime toDue,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., 'createdAt,desc')")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        Pageable pageable = createPageable(page, size, sort);
        
        Page<UserTaskResponse> tasks = userTaskService.getUserTasks(status, q, fromDue, toDue, pageable);
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(summary = "Get task details", description = "Get detailed information about a specific task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserTaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserTaskResponse> getUserTask(
            @Parameter(description = "Task ID") @PathVariable UUID id) {
        
        UserTaskResponse task = userTaskService.getUserTask(id);
        return ResponseEntity.ok(task);
    }
    
    @Operation(summary = "Create new task", description = "Create a new task for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully",
                content = @Content(schema = @Schema(implementation = UserTaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<UserTaskResponse> createTask(
            @Valid @RequestBody UserTaskCreateRequest request) {
        
        UserTaskResponse task = userTaskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }
    
    @Operation(summary = "Update task", description = "Update an existing task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully",
                content = @Content(schema = @Schema(implementation = UserTaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Task not found",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserTaskResponse> updateTask(
            @Parameter(description = "Task ID") @PathVariable UUID id,
            @Valid @RequestBody UserTaskUpdateRequest request) {
        
        UserTaskResponse task = userTaskService.updateTask(id, request);
        return ResponseEntity.ok(task);
    }
    
    @Operation(summary = "Delete task", description = "Delete a task (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID") @PathVariable UUID id) {
        
        userTaskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Change task status", description = "Change the status of a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status changed successfully",
                content = @Content(schema = @Schema(implementation = UserTaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid status or request data",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Task not found",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class))),
        @ApiResponse(responseCode = "409", description = "Invalid status transition",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class)))
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserTaskResponse> changeTaskStatus(
            @Parameter(description = "Task ID") @PathVariable UUID id,
            @Valid @RequestBody UserTaskStatusChangeRequest request) {
        
        Short statusId = Short.parseShort(request.getStatusId());
        UserTaskResponse task = userTaskService.changeTaskStatus(id, statusId);
        return ResponseEntity.ok(task);
    }
    
    
    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParts = sort.split(",");
        String property = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1]) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}