package com.recruitment.repository;

import com.recruitment.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    @Query("SELECT * FROM users ORDER BY id LIMIT :size OFFSET :offset")
    Flux<User> findAllPaged(long offset, int size);
}
