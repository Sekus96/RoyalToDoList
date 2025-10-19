package com.recruitment.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskUpdateRequest {

    private String title;
    private String description;
    private String status;
    private Long userId;
}
