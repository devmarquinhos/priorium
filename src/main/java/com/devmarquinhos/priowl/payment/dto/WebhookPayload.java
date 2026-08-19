package com.devmarquinhos.priowl.payment.dto;

import java.math.BigDecimal;

public record WebhookPayload(String transactionId,
                             Long userId,
                             Long planId,
                             String status,
                             BigDecimal amount) {
}
