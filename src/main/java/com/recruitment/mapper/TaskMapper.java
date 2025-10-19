package com.recruitment.mapper;

import com.recruitment.dto.TaskRequest;
import com.recruitment.dto.TaskResponse;
import com.recruitment.dto.TaskSummaryResponse;
import com.recruitment.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Mapper class for converting between Task entity and Task DTOs.
 */
@Component
public class TaskMapper {

    /**
     * Converts a Task entity to a TaskResponse DTO.
     *
     * @param task the Task entity
     * @return the TaskResponse DTO
     */
    public TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setCreationDate(task.getCreationDate());
        response.setStatus(task.getStatus());
        response.setUserId(task.getUserId());
        return response;
    }

    /**
     * Converts a Task entity to a TaskSummaryResponse DTO.
     *
     * @param task the Task entity
     * @return the TaskSummaryResponse DTO
     */
    public TaskSummaryResponse toSummaryResponse(Task task) {
        TaskSummaryResponse response = new TaskSummaryResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setCreationDate(task.getCreationDate());
        response.setStatus(task.getStatus());
        return response;
    }

    /**
     * Converts a TaskRequest DTO to a Task entity.
     *
     * @param request the TaskRequest DTO
     * @return the Task entity
     */
    public Task toEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCreationDate(LocalDate.now());
        task.setUserId(request.getUserId());
        return task;
    }
}
