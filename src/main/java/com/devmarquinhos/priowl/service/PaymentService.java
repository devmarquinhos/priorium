package com.devmarquinhos.priowl.service;

import com.devmarquinhos.priowl.dto.AdminPaymentResponse;
import com.devmarquinhos.priowl.dto.PaymentResponse;
import com.devmarquinhos.priowl.model.User;
import com.devmarquinhos.priowl.repository.PaymentRepository;
import com.devmarquinhos.priowl.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
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
}