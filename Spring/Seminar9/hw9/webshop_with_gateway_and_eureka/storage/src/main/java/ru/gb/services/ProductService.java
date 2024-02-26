package ru.gb.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.gb.model.Product;
import ru.gb.model.exceptions.ExcessAmountException;
import ru.gb.model.exceptions.ResourceNotFoundException;
import ru.gb.repositories.ProductsRepository;


import java.util.List;

/**
 * Сервис для работы с товарами.
 */
@Service
@AllArgsConstructor

@Transactional(readOnly = true)
public class ProductService {
    /**
     * Объект репозитория.
     */
    private final ProductsRepository productRepository;

    /**
     * Получение всех товаров.
     * @return список товаров.
     */
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    /**
     * Получение данных о конкретном товаре на складе.
     * @param id идентификатор товара.
     * @return объект товара.
     * @throws ResourceNotFoundException исключение при отсутствии товара.
     */
    public Product getProductById(Long id) throws ResourceNotFoundException {
        return productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Товар " + id + " не найден!"));
    }

    /**
     * Уменьшение остатка товара на складе.
     * @param id идентификатор товара.
     * @param amount количество для уменьшения.
     * @throws ExcessAmountException исключение при превышении остатка.
     */

    @Transactional
    public void reduceAmount(@PathVariable Long id, int amount)
            throws ExcessAmountException {
        Product product = getProductById(id);
        if (amount > product.getQuantity())
            throw new ExcessAmountException("Заказ превышает остаток на складе!");
        product.setQuantity(product.getQuantity() - amount);
        product.setReserved(product.getReserved() - amount);
        productRepository.save(product);
    }

    /**
     * Резервирование товара на складе.
     * @param id идентификатор товара.
     * @param amount количество заказа.
     * @throws ExcessAmountException исключение при превышении остатка.
     */
    @Transactional
    public void reservedProduct(Long id, int amount) throws ExcessAmountException{
        Product product = getProductById(id);
        if (amount > product.getQuantity())
            throw new ExcessAmountException("Заказ превышает остаток на складе!");
        product.setReserved(amount);
        productRepository.save(product);
    }

    /**
     * Откат резервирования товара на складе.
     * @param id идентификатор товара.
     * @param amount количество.
     */
    @Transactional
    public void rollbackReservedProduct(Long id, int amount){
        Product product = getProductById(id);
        product.setReserved(product.getReserved() - amount);
        productRepository.save(product);
    }
}
