package com.devmarquinhos.priowl.subscription;

import com.devmarquinhos.priowl.subscription.dto.CheckoutRequest;
import com.devmarquinhos.priowl.subscription.dto.SubscriptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponse> getMySubscription() {
        return ResponseEntity.ok(subscriptionService.getMySubscription());
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request) {
        try {
            return ResponseEntity.ok(subscriptionService.createCheckoutSession(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/me/plan")
    public ResponseEntity<?> changePlan(@RequestBody CheckoutRequest request) {
        try {
            subscriptionService.changeMyPlan(request.planId());
            return ResponseEntity.ok(Collections.singletonMap("message", "Plano alterado com sucesso."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/me/cancel")
    public ResponseEntity<?> cancelSubscription() {
        try {
            subscriptionService.cancelMySubscription();
            return ResponseEntity.ok(Collections.singletonMap("message", "Assinatura cancelada com sucesso."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}