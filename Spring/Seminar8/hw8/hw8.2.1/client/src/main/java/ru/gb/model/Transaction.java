package ru.gb.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Transaction {
    private Long creditNumber;
    private Long debitNumber;
    private BigDecimal balance;

}
