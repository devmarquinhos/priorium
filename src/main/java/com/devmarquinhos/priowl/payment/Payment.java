package com.devmarquinhos.priowl.payment;

import com.devmarquinhos.priowl.subscription.Subscription;
import com.devmarquinhos.priowl.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(nullable = false)
    private BigDecimal amount;

    // PENDING, PAID, FAILED, REFUNDED
    @Column(nullable = false)
    private String status;

    // id of transcation for external gateway
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_date", updatable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();
}