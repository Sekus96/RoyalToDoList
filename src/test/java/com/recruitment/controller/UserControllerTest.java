package com.recruitment.controller;

import com.recruitment.dto.TaskResponse;
import com.recruitment.dto.UserRequest;
import com.recruitment.dto.UserResponse;
import com.recruitment.exception.UserNotFoundException;
import com.recruitment.service.TaskService;
import com.recruitment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(UserController.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private TaskService taskService;

    private UserResponse userResponse;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("John");

        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("Test Task");
        taskResponse.setDescription("Task description");
        taskResponse.setStatus(null);
        taskResponse.setUserId(1L);
    }

    @Test
    void shouldCreateUser() {
        Mockito.when(userService.save(any(UserRequest.class)))
                .thenReturn(Mono.just(userResponse));

        UserRequest request = new UserRequest();
        request.setName("John");

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .value(resp -> assertThat(resp.getName()).isEqualTo("John"));
    }

    @Test
    void shouldFetchAllUsers() {
        Mockito.when(userService.findAll(0, 10)).thenReturn(Flux.just(userResponse));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .hasSize(1)
                .value(list -> assertThat(list.get(0).getName()).isEqualTo("John"));
    }

    @Test
    void shouldGetUserById() {
        Mockito.when(userService.getUserById(1L)).thenReturn(Mono.just(userResponse));

        webTestClient.get()
                .uri("/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(resp -> assertThat(resp.getId()).isEqualTo(1L));
    }

    @Test
    void shouldFetchUserTasks() {
        Mockito.when(userService.getUserTasks(1L))
                .thenReturn(Flux.just(taskResponse));

        webTestClient.get()
                .uri("/users/1/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TaskResponse.class)
                .hasSize(1)
                .value(list -> assertThat(list.get(0).getTitle()).isEqualTo("Test Task"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        Mockito.when(userService.getUserById(2L))
                .thenReturn(Mono.error(new UserNotFoundException("User with id: 2 was not found.")));

        webTestClient.get()
                .uri("/users/2")
                .exchange()
                .expectStatus().isNotFound();
    }
}
