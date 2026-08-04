package com.devmarquinhos.priowl.service;

import com.devmarquinhos.priowl.dto.AdminDashboardResponse;
import com.devmarquinhos.priowl.dto.DashboardResponse;
import com.devmarquinhos.priowl.model.User;
import com.devmarquinhos.priowl.repository.PaymentRepository;
import com.devmarquinhos.priowl.repository.SubscriptionRepository;
import com.devmarquinhos.priowl.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(UserRepository userRepository,
                            SubscriptionRepository subscriptionRepository,
                            PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    private void verifyAdmin() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        if (!user.getIsAdmin()) {
            throw new RuntimeException("Acesso negado. Esta rota é exclusiva para Administradores.");
        }
    }

    public AdminDashboardResponse getDashboardKpis() {
        verifyAdmin();

        Long totalUsers = userRepository.count();

        // counts how many are in active status
        Long activeSubscriptions = subscriptionRepository.countByStatus("ACTIVE");

        // sum of the payments in the last 30d
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        BigDecimal mrr = paymentRepository.sumPaidAmountSince(thirtyDaysAgo);

        // if null, return zero
        if (mrr == null) {
            mrr = BigDecimal.ZERO;
        }

        return new AdminDashboardResponse(totalUsers, activeSubscriptions, mrr);
    }
}