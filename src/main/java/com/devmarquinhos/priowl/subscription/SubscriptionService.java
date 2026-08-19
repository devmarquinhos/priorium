package com.devmarquinhos.priowl.subscription;

import com.devmarquinhos.priowl.subscription.dto.CheckoutRequest;
import com.devmarquinhos.priowl.subscription.dto.CheckoutResponse;
import com.devmarquinhos.priowl.subscription.dto.SubscriptionResponse;
import com.devmarquinhos.priowl.user.User;
import com.devmarquinhos.priowl.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository, PlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
    }

    public SubscriptionResponse getMySubscription() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Subscription sub = subscriptionRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada para este usuário."));

        return new SubscriptionResponse(
                sub.getId(),
                sub.getPlan().getName(),
                sub.getStatus(),
                sub.getStartDate(),
                sub.getEndDate(),
                sub.getPlan().getMaxTasks()
        );
    }

    public CheckoutResponse createCheckoutSession(CheckoutRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado."));

        // mocked data, update later
        String mockCheckoutUrl = "https://gateway.pagamento.com/checkout?userId=" + user.getId() + "&planId=" + plan.getId();

        return new CheckoutResponse(mockCheckoutUrl);
    }

    @Transactional
    public void changeMyPlan(Long newPlanId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Subscription sub = subscriptionRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Você não possui uma assinatura ativa para alterar."));

        Plan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado."));

        sub.setPlan(newPlan);
        subscriptionRepository.save(sub);
    }

    @Transactional
    public void cancelMySubscription() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Subscription sub = subscriptionRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada."));

        sub.setStatus("CANCELLED");
        subscriptionRepository.save(sub);
    }

    private void verifyAdmin() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        if (!user.getIsAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores.");
        }
    }

    public List<SubscriptionResponse> getAllSubscriptionsForAdmin() {
        verifyAdmin();
        return subscriptionRepository.findAll().stream()
                .map(sub -> new SubscriptionResponse(
                        sub.getId(), sub.getPlan().getName(), sub.getStatus(),
                        sub.getStartDate(), sub.getEndDate(), sub.getPlan().getMaxTasks()
                ))
                .toList();
    }

    public SubscriptionResponse getSubscriptionByIdForAdmin(Long id) {
        verifyAdmin();
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada."));

        return new SubscriptionResponse(
                sub.getId(), sub.getPlan().getName(), sub.getStatus(),
                sub.getStartDate(), sub.getEndDate(), sub.getPlan().getMaxTasks()
        );
    }
}