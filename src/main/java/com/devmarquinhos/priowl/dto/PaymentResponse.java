package com.devmarquinhos.priowl.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        BigDecimal amount,
        String status,
        LocalDateTime paymentDate,
        String transactionId
) {
}
