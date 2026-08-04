package com.devmarquinhos.priowl.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserIdOrderByPaymentDateDesc(Long userId);
    List<Payment> findAllByOrderByPaymentDateDesc();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PAID' AND p.paymentDate >= :startDate")
    BigDecimal sumPaidAmountSince(@Param("startDate") LocalDateTime startDate);
}
