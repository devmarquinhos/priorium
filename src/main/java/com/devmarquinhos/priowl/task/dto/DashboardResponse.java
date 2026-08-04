package com.devmarquinhos.priowl.task.dto;

public record DashboardResponse(
        Double overallProgress,
        Long completedTasks,
        Long totalActiveTasks,
        Long cancelledTasks,
        Long inProgressTasks,
        Long pendingTasks
) {
}
