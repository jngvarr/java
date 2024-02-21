package ru.gb.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Order {
    private Long id;
    private List<Product> goods;
    private String description;
    private BigDecimal sumPrice;
}
