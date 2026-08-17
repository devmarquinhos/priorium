package com.devmarquinhos.priowl.admin;

import com.devmarquinhos.priowl.admin.dto.AdminDashboardResponse;
import com.devmarquinhos.priowl.payment.PaymentService;
import com.devmarquinhos.priowl.subscription.SubscriptionService;
import com.devmarquinhos.priowl.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PaymentService paymentService;
    private final DashboardService dashboardService;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public AdminController(PaymentService paymentService, DashboardService dashboardService, UserService userService, SubscriptionService subscriptionService) {
        this.paymentService = paymentService;
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getAllPayments() {
        try {
            return ResponseEntity.ok(paymentService.getAllPaymentsForAdmin());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/dashboard/kpis")
    public ResponseEntity<AdminDashboardResponse> getDashboardKpis() {
        try {
            return ResponseEntity.ok(dashboardService.getDashboardKpis());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsersForAdmin());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserByIdForAdmin(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<?> getAllSubscriptions() {
        try {
            return ResponseEntity.ok(subscriptionService.getAllSubscriptionsForAdmin());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/subscriptions/{id}")
    public ResponseEntity<?> getSubscriptionById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(subscriptionService.getSubscriptionByIdForAdmin(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}