package com.devmarquinhos.priowl.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminPaymentResponse(
        Long id,
        String userName,
        String userEmail,
        BigDecimal amount,
        String status,
        LocalDateTime paymentDate,
        String transactionId
) {
}
