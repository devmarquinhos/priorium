package com.devmarquinhos.priowl.payment;

import com.devmarquinhos.priowl.payment.dto.WebhookPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/payments")
public class WebhookController {

    private final PaymentService paymentService;

    public WebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{provider}")
    public ResponseEntity<Void> handleWebhook(@PathVariable String provider, @RequestBody WebhookPayload payload) {
        paymentService.processWebhook(provider, payload);
        return ResponseEntity.ok().build();
    }
}