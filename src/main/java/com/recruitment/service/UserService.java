package com.recruitment.service;

import com.recruitment.dto.TaskResponse;
import com.recruitment.dto.UserRequest;
import com.recruitment.dto.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponse> save(UserRequest request);

    Flux<UserResponse> findAll(int page, int size);

    Mono<UserResponse> getUserById(Long id);

    Flux<TaskResponse> getUserTasks(Long userId);

}
