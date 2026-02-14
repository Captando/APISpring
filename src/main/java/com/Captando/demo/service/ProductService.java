package com.Captando.demo.service;

import com.Captando.demo.dto.ProductRequest;
import com.Captando.demo.dto.ProductResponse;
import com.Captando.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> findAll(String name, String category, Double minPrice, Double maxPrice, Boolean active, Pageable pageable);
    ProductResponse findById(Long id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    ProductResponse adjustStock(Long id, int delta);
    void delete(Long id);

    static Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStockQuantity(request.getStockQuantity());
        product.setActive(Boolean.TRUE.equals(request.getActive()));
        return product;
    }

    static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStockQuantity(),
                product.isActive()
        );
    }
}
