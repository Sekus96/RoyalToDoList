package com.recruitment.repository;

import com.recruitment.entity.Task;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TaskRepository extends ReactiveCrudRepository<Task, Long> {
    Flux<Task> findByUserId(Long userId);

    @Query("SELECT * FROM tasks ORDER BY id LIMIT :size OFFSET :offset")
    Flux<Task> findAllPaged(long offset, int size);
}
