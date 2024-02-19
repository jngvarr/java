package ru.gb.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Объект для проведения транзакции.
 */
@Data
public class Transaction {
    private long buyerAccountId;
    private BigDecimal sum;
}

