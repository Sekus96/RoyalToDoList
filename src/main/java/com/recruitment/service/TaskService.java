package com.recruitment.service;

import com.recruitment.dto.TaskRequest;
import com.recruitment.dto.TaskResponse;
import com.recruitment.dto.TaskSummaryResponse;
import com.recruitment.dto.TaskUpdateRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService {

    Mono<TaskResponse> save(TaskRequest request);

    Flux<TaskSummaryResponse> findAll(int page, int size);

    Mono<TaskResponse> getTaskById(Long id);

    Mono<TaskResponse> updateTask(TaskUpdateRequest taskUpdate, Long id);

    Mono<Void> deleteTask(Long id);

    Mono<TaskResponse> partialUpdate(TaskUpdateRequest taskUpdate, Long id);

    Mono<TaskResponse> assignTaskToUser(Long taskId, Long userId);

}
