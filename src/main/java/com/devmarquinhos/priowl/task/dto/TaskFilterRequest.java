package com.devmarquinhos.priowl.task.dto;

import com.devmarquinhos.priowl.task.TaskStatus;

import java.time.LocalDateTime;

public record TaskFilterRequest(
        TaskStatus status,
        Integer importance,
        String title,
        LocalDateTime deadlineBefore
) {
}
