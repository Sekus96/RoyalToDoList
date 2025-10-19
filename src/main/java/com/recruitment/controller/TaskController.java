package com.recruitment.controller;

import com.recruitment.dto.TaskRequest;
import com.recruitment.dto.TaskResponse;
import com.recruitment.dto.TaskSummaryResponse;
import com.recruitment.dto.TaskUpdateRequest;
import com.recruitment.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing tasks.
 * Provides endpoints for creating, updating, deleting, and fetching tasks.
 */
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a new task.
     *
     * @param taskRequest the task request DTO
     * @return a Mono emitting the created TaskResponse
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new task")
    public Mono<TaskResponse> createTask(@RequestBody TaskRequest taskRequest) {
        return taskService.save(taskRequest);
    }

    /**
     * Fetches all tasks with summary information.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a Flux emitting TaskSummaryResponse objects
     */
    @GetMapping
    @Operation(summary = "Fetches all tasks")
    public Flux<TaskSummaryResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return taskService.findAll(page, size);
    }

    /**
     * Fetches task details by ID.
     *
     * @param id the ID of the task
     * @return a Mono emitting the TaskResponse object
     */
    @GetMapping("/{id}")
    @Operation(summary = "Fetches task details by ID")
    public Mono<TaskResponse> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    /**
     * Updates the entire task.
     *
     * @param taskUpdate the DTO with updated fields
     * @param id         the ID of the task to update
     * @return a Mono emitting the updated TaskResponse
     */
    @PutMapping("/{id}")
    @Operation(summary = "Updates task details")
    public Mono<TaskResponse> updateTask(@RequestBody TaskUpdateRequest taskUpdate, @PathVariable Long id) {
        return taskService.updateTask(taskUpdate, id);
    }

    /**
     * Partially updates the task (only non-null fields are updated).
     *
     * @param taskUpdateRequest the DTO containing fields to update
     * @param id                the ID of the task
     * @return a Mono emitting the updated TaskResponse
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Partially updates task details")
    public Mono<TaskResponse> partialUpdate(@RequestBody TaskUpdateRequest taskUpdateRequest, @PathVariable Long id) {
        return taskService.partialUpdate(taskUpdateRequest, id);
    }

    /**
     * Deletes a task by ID.
     *
     * @param id the ID of the task to delete
     * @return a Mono indicating completion
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes task by ID")
    public Mono<Void> deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id);
    }

    /**
     * Assigns a task to a user asynchronously.
     *
     * @param taskId taskId the ID of the task to assign
     * @param userId the ID of the user to whom the task will be assigned
     * @return a Mono emitting the updated TaskResponse
     */
    @PutMapping("/{taskId}/assign/{userId}")
    @Operation(summary = "Assigns a task to a user asynchronously")
    public Mono<TaskResponse> assignTask(@PathVariable Long taskId, @PathVariable Long userId) {
        return taskService.assignTaskToUser(taskId, userId);
    }
}
