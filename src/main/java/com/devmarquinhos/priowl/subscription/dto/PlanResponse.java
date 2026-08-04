package com.devmarquinhos.priowl.subscription.dto;

import java.math.BigDecimal;

public record PlanResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer maxTasks,
        Boolean isActive
) {
}
