package gb.hw82.services;

import gb.hw82.model.Product;
import gb.hw82.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ShopServiceImpl implements ShopService {
    private final ProductRepository repository;

    @Override
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @Override
    public Product getProductById(long id) {
        return repository.findById(id).orElseThrow(null);
    }

    @Override
    public Product addTask(Product product) {
        return null;
    }

    @Override
    public void deleteProduct(long id) {

    }
}
