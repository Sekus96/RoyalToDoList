package com.recruitment.service;

import com.recruitment.dto.TaskRequest;
import com.recruitment.dto.TaskResponse;
import com.recruitment.dto.TaskSummaryResponse;
import com.recruitment.dto.TaskUpdateRequest;
import com.recruitment.entity.Task;
import com.recruitment.enums.TaskStatus;
import com.recruitment.exception.InvalidTaskDataException;
import com.recruitment.exception.StatusNotFoundException;
import com.recruitment.exception.TaskNotFoundException;
import com.recruitment.exception.UserNotFoundException;
import com.recruitment.mapper.TaskMapper;
import com.recruitment.repository.TaskRepository;
import com.recruitment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Implementation of TaskService interface.
 * Provides reactive methods for managing tasks in the system.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    /**
     * Saves a new task. Validates user existence if userId is provided.
     *
     * @param request the task request DTO
     * @return a Mono emitting the created TaskResponse
     * @throws UserNotFoundException if userId is provided but the user does not exist
     */
    @Override
    public Mono<TaskResponse> save(TaskRequest request) {
        Task task = taskMapper.toEntity(request);
        task.setCreationDate(LocalDate.now());
        task.setStatus(TaskStatus.NEW);

        if (task.getUserId() != null) {
            return userRepository.findById(task.getUserId())
                    .switchIfEmpty(Mono.error(new UserNotFoundException(
                            "User with id " + task.getUserId() + " was not found."
                    )))
                    .then(taskRepository.save(task))
                    .map(taskMapper::toResponse);
        }

        return taskRepository.save(task)
                .map(taskMapper::toResponse);
    }

    /**
     * Returns all tasks in the system as summary DTOs.
     *
     * @return a Flux of TaskSummaryResponse
     */
    @Override
    public Flux<TaskSummaryResponse> findAll(int page, int size) {
        long offset = (long) page * size;
        return taskRepository.findAllPaged(offset, size)
                .map(taskMapper::toSummaryResponse);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task
     * @return a Mono emitting the TaskResponse
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public Mono<TaskResponse> getTaskById(Long id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task with id: " + id + " was not found.")))
                .map(taskMapper::toResponse);
    }

    /**
     * Updates an existing task with full data.
     *
     * @param taskUpdate the task update DTO
     * @param id         the ID of the task to update
     * @return a Mono emitting the updated TaskResponse
     * @throws TaskNotFoundException    if the task does not exist
     * @throws InvalidTaskDataException if any required field is missing
     * @throws StatusNotFoundException  if status value is invalid
     */
    @Override
    public Mono<TaskResponse> updateTask(TaskUpdateRequest taskUpdate, Long id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task with id: " + id + " was not found.")))
                .flatMap(existingTask -> {
                    TaskStatus status = validateTaskUpdate(taskUpdate);
                    existingTask.setTitle(taskUpdate.getTitle());
                    existingTask.setDescription(taskUpdate.getDescription());
                    existingTask.setStatus(status);
                    return taskRepository.save(existingTask)
                            .map(taskMapper::toResponse);
                });

    }

    /**
     * Applies partial update to an existing task.
     * Only non-null and non-blank fields are updated.
     *
     * @param taskUpdate the DTO containing fields to update
     * @param id         the ID of the task to update
     * @return a Mono emitting the updated TaskResponse
     * @throws TaskNotFoundException   if the task does not exist
     * @throws StatusNotFoundException if the status value is invalid
     */
    @Override
    public Mono<TaskResponse> partialUpdate(TaskUpdateRequest taskUpdate, Long id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task with id: " + id + " was not found.")))
                .flatMap(existingTask -> updateTaskFields(existingTask, taskUpdate))
                .flatMap(taskRepository::save)
                .map(taskMapper::toResponse);
    }

    /**
     * Deletes a task by ID.
     *
     * @param id the ID of the task to delete
     * @return a Mono indicating completion
     * @throws TaskNotFoundException if the task does not exist
     */
    @Override
    public Mono<Void> deleteTask(Long id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task with id: " + id + " was not found.")))
                .flatMap(existingTask -> taskRepository.deleteById(id));
    }

    /**
     * Validates the TaskUpdateRequest and returns TaskStatus enum.
     *
     * @param taskUpdate the update DTO
     * @return TaskStatus enum
     * @throws InvalidTaskDataException if required fields are missing
     * @throws StatusNotFoundException  if status is invalid
     */
    private TaskStatus validateTaskUpdate(TaskUpdateRequest taskUpdate) {
        if (taskUpdate.getTitle() == null || taskUpdate.getTitle().isBlank()) {
            throw new InvalidTaskDataException("Title cannot be empty");
        }
        if (taskUpdate.getDescription() == null || taskUpdate.getDescription().isBlank()) {
            throw new InvalidTaskDataException("Description cannot be empty");
        }
        if (taskUpdate.getStatus() == null || taskUpdate.getStatus().isBlank()) {
            throw new InvalidTaskDataException("Status cannot be empty");
        }

        try {
            return TaskStatus.valueOf(taskUpdate.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new StatusNotFoundException(
                    "Wrong status. Choose one from the list: NEW, IN_PROGRESS, COMPLETED, CANCELLED."
            );
        }
    }

    /**
     * Helper method to apply partial updates to a Task entity.
     *
     * @param existingTask the existing Task entity
     * @param taskUpdate   the DTO containing fields to update
     * @return a Mono emitting the updated TaskResponse
     */
    private Mono<Task> updateTaskFields(Task existingTask, TaskUpdateRequest taskUpdate) {
        if (taskUpdate.getTitle() != null && !taskUpdate.getTitle().isBlank()) {
            existingTask.setTitle(taskUpdate.getTitle());
        }

        if (taskUpdate.getDescription() != null && !taskUpdate.getDescription().isBlank()) {
            existingTask.setDescription(taskUpdate.getDescription());
        }

        if (taskUpdate.getStatus() != null && !taskUpdate.getStatus().isBlank()) {
            try {
                TaskStatus status = TaskStatus.valueOf(taskUpdate.getStatus().toUpperCase());
                existingTask.setStatus(status);
            } catch (IllegalArgumentException e) {
                return Mono.error(new StatusNotFoundException(
                        "Wrong status. Choose one from the list: NEW, IN_PROGRESS, COMPLETED, CANCELLED."
                ));
            }
        }

        return Mono.just(existingTask);
    }
}
