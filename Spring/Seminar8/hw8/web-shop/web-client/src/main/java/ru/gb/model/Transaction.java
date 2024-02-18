package ru.gb.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Transaction {
    private long buyerAccountId;
    private BigDecimal sum;
}
