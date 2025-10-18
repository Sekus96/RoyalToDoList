package com.recruitment.entity;

import com.recruitment.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Getter
@Setter
@Table("tasks")
public class Task {

    @Id
    private Long id;
    private String title;
    private String description;
    private LocalDate creationDate;
    private TaskStatus status;
    @Column("user_id")
    private Long userId;
}
