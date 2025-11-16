package com.billyow.app.boardang.task.DTO;

public record MoveTaskRequest(
        Long taskId,
        Long newColumnId
) {
}
