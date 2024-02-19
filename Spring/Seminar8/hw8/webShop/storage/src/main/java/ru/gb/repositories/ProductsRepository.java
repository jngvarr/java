package ru.gb.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.model.Product;

public interface ProductsRepository extends JpaRepository<Product, Long> {
}
