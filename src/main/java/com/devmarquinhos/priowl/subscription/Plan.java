package com.devmarquinhos.priowl.subscription;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "max_tasks", nullable = false)
    private Integer maxTasks;

    @Column(name = "is_active")
    private Boolean isActive;
}
