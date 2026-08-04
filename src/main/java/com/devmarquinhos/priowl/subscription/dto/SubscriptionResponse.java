package com.devmarquinhos.priowl.subscription.dto;

import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long id,
        String planName,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer maxTasks
) {
}
