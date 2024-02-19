package ru.gb.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Сущность аккаунта.
 */
@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "number", nullable = false)
    private Long number;
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
    @Column(name = "description", nullable = false)
    private String description;
}