package ru.gb.web_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Order<Product> { //Todo развернуть Order
    private Long id;
    private List<Product> goods;
    private String description;
    private BigDecimal sumPrice;

    private int productsAmount;
}
