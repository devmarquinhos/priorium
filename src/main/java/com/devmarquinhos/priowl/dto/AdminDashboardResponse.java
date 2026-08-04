package com.devmarquinhos.priowl.dto;

import java.math.BigDecimal;

public record AdminDashboardResponse(
        Long totalUsers,
        Long activeSubscriptions,
        BigDecimal mrr
) {}