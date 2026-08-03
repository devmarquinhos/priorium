package com.devmarquinhos.priowl.service;

import com.devmarquinhos.priowl.dto.SubscriptionResponse;
import com.devmarquinhos.priowl.model.Subscription;
import com.devmarquinhos.priowl.model.User;
import com.devmarquinhos.priowl.repository.SubscriptionRepository;
import com.devmarquinhos.priowl.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    public SubscriptionResponse getMySubscription() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Optional<Subscription> subOpt = subscriptionRepository.findByUserId(user.getId());

        if (subOpt.isPresent()) {
            Subscription sub = subOpt.get();
            return new SubscriptionResponse(
                    sub.getId(),
                    sub.getPlan().getName(),
                    sub.getStatus(),
                    sub.getStartDate(),
                    sub.getEndDate(),
                    sub.getPlan().getMaxTasks()
            );
        } else {
            // new users starts with free plan
            return new SubscriptionResponse(
                    null,
                    "Free Plan",
                    "ACTIVE",
                    null,
                    null,
                    5
            );
        }
    }
}