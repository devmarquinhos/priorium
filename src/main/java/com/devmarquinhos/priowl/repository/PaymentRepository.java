package com.devmarquinhos.priowl.repository;

import com.devmarquinhos.priowl.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserIdOrderByPaymentDateDesc(Long userId);

    List<Payment> findAllByOrderByPaymentDateDesc();
}
