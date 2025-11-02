package com.meetus.MeetUSInterview.controller;

import com.meetus.MeetUSInterview.dto.request.task.TaskCreateRequest;
import com.meetus.MeetUSInterview.dto.request.task.TaskSearchRequest;
import com.meetus.MeetUSInterview.dto.response.APIResponse;
import com.meetus.MeetUSInterview.dto.response.task.TaskPageResponse;
import com.meetus.MeetUSInterview.dto.response.task.TaskResponse;
import com.meetus.MeetUSInterview.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Task operations
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Task Management", description = "APIs for managing user tasks")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new task for the authenticated user
     * @param request task create request
     * @return created task response
     */
    @PostMapping
    @Operation(
        summary = "Create a new task",
        description = "Creates a new task for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Task created successfully",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        )
    })
    public ResponseEntity<APIResponse<TaskResponse>> createTask(@Valid @RequestBody TaskCreateRequest request) {
        Long userId = getCurrentUserId();
        log.info("Received request to create task for user ID: {}", userId);

        TaskResponse response = taskService.createTask(request, userId);

        log.info("Task created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(response, "Task created successfully"));
    }

    /**
     * Get all tasks for the authenticated user with pagination and filtering
     * @param searchRequest search criteria
     * @return paginated task response
     */
    @GetMapping
    @Operation(
        summary = "Get all tasks",
        description = "Retrieves all tasks for the authenticated user with optional filtering and pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        )
    })
    public ResponseEntity<APIResponse<TaskPageResponse>> getAllTasks(@Valid TaskSearchRequest searchRequest) {
        Long userId = getCurrentUserId();
        log.info("Received request to get tasks for user ID: {} with filters - status: {}, search: {}, fromDate: {}, toDate: {}, sortBy: {}, sortDirection: {}, page: {}, size: {}",
               userId, searchRequest.getStatus(), searchRequest.getSearch(), searchRequest.getFromDate(), 
               searchRequest.getToDate(), searchRequest.getSortBy(), searchRequest.getSortDirection(), 
               searchRequest.getPage(), searchRequest.getSize());
        
        TaskPageResponse response = taskService.getTasks(searchRequest, userId);
        
        log.info("Retrieved {} tasks out of {} total for user ID: {}", 
            response.getTasks().size(), 
            response.getTotalElements(),
            userId);
        
        return ResponseEntity.ok(APIResponse.success(response, "Tasks retrieved successfully"));
    }

    /**
     * Get a single task by ID for the authenticated user
     * @param id task ID
     * @return task response
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get task by ID",
        description = "Retrieves a specific task by ID for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Task retrieved successfully",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - Task belongs to another user",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Task not found",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        )
    })
    public ResponseEntity<APIResponse<TaskResponse>> getTaskById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        log.info("Received request to get task with ID: {} for user ID: {}", id, userId);

        TaskResponse response = taskService.getTaskById(id, userId);

        log.info("Task retrieved successfully with ID: {}", id);
        return ResponseEntity.ok(APIResponse.success(response, "Task retrieved successfully"));
    }

    /**
     * Update a task status from OPEN to DONE for the authenticated user
     * @param id task ID
     * @return updated task response
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Mark task as done",
        description = "Updates a task status from OPEN to DONE. Returns error if task is already DONE."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Task updated successfully",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - Task belongs to another user",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Task not found",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflict - Task status is already done",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        )
    })
    public ResponseEntity<APIResponse<TaskResponse>> updateTask(
            @PathVariable Long id ) {
        Long userId = getCurrentUserId();
        log.info("Received request to update task with ID: {} for user ID: {}", id, userId);

        TaskResponse response = taskService.updateTask(id, userId);

        log.info("Task updated successfully with ID: {}", id);
        return ResponseEntity.ok(APIResponse.success(response, "Task updated successfully"));
    }

    /**
     * Delete a task for the authenticated user
     * @param id task ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete task",
        description = "Deletes a task for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Task deleted successfully",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - Task belongs to another user",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Task not found",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        )
    })
    public ResponseEntity<APIResponse<Void>> deleteTask(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        log.info("Received request to delete task with ID: {} for user ID: {}", id, userId);

        taskService.deleteTask(id, userId);

        log.info("Task deleted successfully with ID: {}", id);
        return ResponseEntity.ok(APIResponse.success(null, "Task deleted successfully"));
    }

    /**
     * Get current authenticated user's ID from JWT token
     * @return user ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName()); // User.getUsername() returns user ID as string
    }
}
