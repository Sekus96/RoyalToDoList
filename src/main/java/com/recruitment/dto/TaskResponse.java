package com.recruitment.dto;

import com.recruitment.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate creationDate;
    private TaskStatus status;
    private Long userId;
}
