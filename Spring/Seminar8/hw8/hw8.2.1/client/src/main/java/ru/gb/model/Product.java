package ru.gb.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Long quantity;
}
