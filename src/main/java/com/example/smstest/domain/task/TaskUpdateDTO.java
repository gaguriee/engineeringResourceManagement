package com.example.smstest.domain.task;

import lombok.Data;

@Data
public class TaskUpdateDTO {
    private Long taskId;
    private String editedContent;
}
