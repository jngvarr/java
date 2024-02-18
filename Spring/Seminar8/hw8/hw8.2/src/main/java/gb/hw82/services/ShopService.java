package gb.hw82.services;

import gb.hw82.model.Product;

import java.util.List;

public interface ShopService {
    List<Product> getAllProducts();

    Product getProductById(long id);

    Product addProduct(Product product);

    void deleteProduct(long id);
}
