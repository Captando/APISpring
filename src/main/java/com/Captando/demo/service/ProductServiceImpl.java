package com.Captando.demo.service;

import com.Captando.demo.dto.ProductRequest;
import com.Captando.demo.dto.ProductResponse;
import com.Captando.demo.model.Product;
import com.Captando.demo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<ProductResponse> findAll(String name, Pageable pageable) {
        Page<Product> page;
        if (StringUtils.hasText(name)) {
            page = productRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else {
            page = productRepository.findAll(pageable);
        }
        return page.map(ProductService::toResponse);
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductService.toResponse(product);
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        Product product = ProductService.toEntity(request);
        product.setId(null);
        Product saved = productRepository.save(product);
        return ProductService.toResponse(saved);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        Product updated = productRepository.save(existing);
        return ProductService.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(existing);
    }
}
