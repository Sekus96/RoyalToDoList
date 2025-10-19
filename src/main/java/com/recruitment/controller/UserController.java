package com.recruitment.controller;

import com.recruitment.dto.TaskResponse;
import com.recruitment.dto.UserRequest;
import com.recruitment.dto.UserResponse;
import com.recruitment.service.TaskService;
import com.recruitment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing users.
 * Provides endpoints for creating users, fetching user details, and fetching user tasks.
 */
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    /**
     * Creates a new user.
     *
     * @param userRequest the user request DTO
     * @return a Mono emitting the created UserResponse
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new user")
    public Mono<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        return userService.save(userRequest);
    }

    /**
     * Fetches all users in the system.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a Flux emitting UserResponse objects
     */
    @GetMapping
    @Operation(summary = "Fetches all users")
    public Flux<UserResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.findAll(page, size);
    }

    /**
     * Fetches user details by ID.
     *
     * @param id the ID of the user
     * @return a Mono emitting the UserResponse object
     */
    @GetMapping("/{id}")
    @Operation(summary = "Fetches user details by ID")
    public Mono<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * Fetches tasks assigned to a specific user.
     *
     * @param id the ID of the user
     * @return a Flux emitting TaskResponse objects
     */
    @GetMapping("/{id}/tasks")
    @Operation(summary = "Fetches user tasks by ID")
    public Flux<TaskResponse> getUserTasks(@PathVariable Long id) {
        return userService.getUserTasks(id);
    }
}
