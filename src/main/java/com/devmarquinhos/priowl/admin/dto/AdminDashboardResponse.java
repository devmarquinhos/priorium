package com.devmarquinhos.priowl.admin.dto;

import java.math.BigDecimal;

public record AdminDashboardResponse(
        Long totalUsers,
        Long activeSubscriptions,
        BigDecimal mrr
) {}