package com.devmarquinhos.priowl.payment;

import com.devmarquinhos.priowl.payment.dto.AdminPaymentResponse;
import com.devmarquinhos.priowl.payment.dto.PaymentResponse;
import com.devmarquinhos.priowl.subscription.Plan;
import com.devmarquinhos.priowl.subscription.PlanRepository;
import com.devmarquinhos.priowl.subscription.Subscription;
import com.devmarquinhos.priowl.subscription.SubscriptionRepository;
import com.devmarquinhos.priowl.user.User;
import com.devmarquinhos.priowl.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;

    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository, SubscriptionRepository subscriptionRepository, PlanRepository planRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    // only returns user payments
    public List<PaymentResponse> getMyPayments() {
        User user = getAuthenticatedUser();

        return paymentRepository.findByUserIdOrderByPaymentDateDesc(user.getId())
                .stream()
                .map(p -> new PaymentResponse(
                        p.getId(), p.getAmount(), p.getStatus(),
                        p.getPaymentDate(), p.getTransactionId()
                ))
                .collect(Collectors.toList());
    }

    // returns all payments of the platform, only for admins
    public List<AdminPaymentResponse> getAllPaymentsForAdmin() {
        User user = getAuthenticatedUser();
        if (!user.getIsAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem ver o fluxo de caixa.");
        }

        return paymentRepository.findAllByOrderByPaymentDateDesc()
                .stream()
                .map(p -> new AdminPaymentResponse(
                        p.getId(), p.getUser().getUsername(), p.getUser().getEmail(),
                        p.getAmount(), p.getStatus(), p.getPaymentDate(), p.getTransactionId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void processWebhook(String provider, com.devmarquinhos.priowl.payment.dto.WebhookPayload payload) {
        User user = userRepository.findById(payload.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no Webhook."));

        Plan plan = planRepository.findById(payload.planId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado no Webhook."));

        Subscription subscription =
                subscriptionRepository.findByUserId(user.getId())
                        .orElse(new Subscription());

        subscription.setUser(user);
        subscription.setPlan(plan);

        if ("PAID".equalsIgnoreCase(payload.status())) {
            subscription.setStatus("ACTIVE");
            subscription.setEndDate(java.time.LocalDateTime.now().plusDays(30));
        } else {
            subscription.setStatus("PENDING_PAYMENT");
        }
        subscriptionRepository.save(subscription);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setSubscription(subscription);
        payment.setAmount(payload.amount());
        payment.setStatus(payload.status());
        payment.setTransactionId(payload.transactionId());
        paymentRepository.save(payment);
    }
}