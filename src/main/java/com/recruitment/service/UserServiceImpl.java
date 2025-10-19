package com.recruitment.service;

import com.recruitment.dto.TaskResponse;
import com.recruitment.dto.UserRequest;
import com.recruitment.dto.UserResponse;
import com.recruitment.entity.User;
import com.recruitment.exception.UserNotFoundException;
import com.recruitment.mapper.TaskMapper;
import com.recruitment.mapper.UserMapper;
import com.recruitment.repository.TaskRepository;
import com.recruitment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of UserService interface.
 * Provides reactive methods for managing users in the system.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    /**
     * Saves a new user in the system.
     *
     * @param request the user request DTO
     * @return a Mono emitting the created UserResponse
     */
    @Transactional
    @Override
    public Mono<UserResponse> save(UserRequest request) {
        User user = userMapper.toEntity(request);
        return userRepository.save(user)
                .map(userMapper::toResponse);
    }

    /**
     * Fetches all users.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a Flux emitting UserResponse objects
     */
    @Override
    public Flux<UserResponse> findAll(int page, int size) {
        long offset = (long) page * size;
        return userRepository.findAllPaged(offset, size)
                .map(userMapper::toResponse);
    }

    /**
     * Fetches user details by ID.
     *
     * @param id the ID of the user
     * @return a Mono emitting the UserResponse object
     * @throws UserNotFoundException if the user does not exist
     */
    @Override
    public Mono<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id: " + id + " was not found.")))
                .map(userMapper::toResponse);
    }

    /**
     * Fetches tasks assigned to a specific user.
     *
     * @param userId the ID of the user
     * @return a Flux emitting TaskResponse objects
     * @throws UserNotFoundException if the user does not exist
     */
    @Override
    public Flux<TaskResponse> getUserTasks(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id: " + userId + " was not found.")))
                .thenMany(taskRepository.findByUserId(userId))
                .map(taskMapper::toResponse);
    }
}
