package com.Captando.demo.service;

import com.Captando.demo.model.Product;
import com.Captando.demo.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product create(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    @Override
    public Product update(Long id, Product product) {
        Product existing = findById(id);
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        return productRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Product existing = findById(id);
        productRepository.delete(existing);
    }
}
