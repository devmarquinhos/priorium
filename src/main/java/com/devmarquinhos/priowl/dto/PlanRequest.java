package com.devmarquinhos.priowl.dto;

import java.math.BigDecimal;

public record PlanRequest(
        String name,
        String description,
        BigDecimal price,
        Integer maxTasks
) {
}
