package ru.gb.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.model.Product;
@Repository
public interface ProductsRepository extends JpaRepository<Product, Long> {
}
