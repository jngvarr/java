package ru.gb.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
    public class Product {
        private Long id;
        private String title;
        private String description;
        private BigDecimal price;
        private int quantity;
    }

