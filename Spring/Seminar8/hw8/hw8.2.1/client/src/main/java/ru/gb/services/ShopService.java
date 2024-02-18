package ru.gb.services;

import gb.hw82.model.Product;

import java.util.List;

public interface ShopService {
    List<Product> getAllProducts();

    Product getProductById(long id);

    Product addTask(Product product);

    void deleteProduct(long id);
}
