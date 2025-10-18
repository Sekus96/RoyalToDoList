package com.recruitment.controller;

import com.recruitment.dto.TaskRequest;
import com.recruitment.dto.TaskResponse;
import com.recruitment.dto.TaskSummaryResponse;
import com.recruitment.dto.TaskUpdateRequest;
import com.recruitment.enums.TaskStatus;
import com.recruitment.exception.TaskNotFoundException;
import com.recruitment.service.TaskService;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TaskService taskService;

    private TaskResponse taskResponse;
    private TaskSummaryResponse taskSummaryResponse;

    @BeforeEach
    void setUp() {
        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("Test Task");
        taskResponse.setDescription("Task description");
        taskResponse.setUserId(1L);
        taskResponse.setStatus(TaskStatus.NEW);
        taskResponse.setCreationDate(LocalDate.now());

        taskSummaryResponse = new TaskSummaryResponse();
        taskSummaryResponse.setId(1L);
        taskSummaryResponse.setTitle("Test Task");
        taskSummaryResponse.setStatus(TaskStatus.NEW);
        taskSummaryResponse.setCreationDate(LocalDate.now());
    }

    @Test
    void shouldCreateTask() {
        Mockito.when(taskService.save(any(TaskRequest.class)))
                .thenReturn(Mono.just(taskResponse));

        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Task description");
        request.setUserId(1L);

        webTestClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TaskResponse.class)
                .value(resp -> assertThat(resp.getTitle()).isEqualTo("Test Task"));
    }

    @Test
    void shouldFetchAllTasks() {
        Mockito.when(taskService.findAll(0, 10)).thenReturn(Flux.just(taskSummaryResponse));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tasks")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TaskSummaryResponse.class)
                .hasSize(1)
                .value(list -> assertThat(list.get(0).getTitle()).isEqualTo("Test Task"));
    }

    @Test
    void shouldGetTaskById() {
        Mockito.when(taskService.getTaskById(1L)).thenReturn(Mono.just(taskResponse));

        webTestClient.get()
                .uri("/tasks/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskResponse.class)
                .value(resp -> assertThat(resp.getId()).isEqualTo(1L));
    }

    @Test
    void shouldUpdateTask() {
        Mockito.when(taskService.updateTask(any(TaskUpdateRequest.class), eq(1L)))
                .thenReturn(Mono.just(taskResponse));

        TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated description");
        updateRequest.setStatus("IN_PROGRESS");

        webTestClient.put()
                .uri("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskResponse.class)
                .value(resp -> assertThat(resp.getTitle()).isEqualTo("Test Task"));
    }

    @Test
    void shouldPartialUpdateTask() {
        Mockito.when(taskService.partialUpdate(any(TaskUpdateRequest.class), eq(1L)))
                .thenReturn(Mono.just(taskResponse));

        TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle("Partially Updated");

        webTestClient.patch()
                .uri("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskResponse.class)
                .value(resp -> assertThat(resp.getTitle()).isEqualTo("Test Task"));
    }

    @Test
    void shouldDeleteTask() {
        Mockito.when(taskService.deleteTask(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/tasks/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() {
        Mockito.when(taskService.getTaskById(2L))
                .thenReturn(Mono.error(new TaskNotFoundException("Task with id: 2 was not found.")));

        webTestClient.get()
                .uri("/tasks/2")
                .exchange()
                .expectStatus().isNotFound();
    }
}
