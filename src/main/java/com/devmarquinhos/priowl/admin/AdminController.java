package com.devmarquinhos.priowl.admin;

import com.devmarquinhos.priowl.admin.dto.AdminDashboardResponse;
import com.devmarquinhos.priowl.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PaymentService paymentService;
    private final DashboardService dashboardService;

    public AdminController(PaymentService paymentService, DashboardService dashboardService) {
        this.paymentService = paymentService;
        this.dashboardService = dashboardService;
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
}