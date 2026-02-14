package com.Captando.demo.service;

import com.Captando.demo.dto.ProductRequest;
import com.Captando.demo.dto.ProductResponse;
import com.Captando.demo.model.Product;
import com.Captando.demo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(String name, String category, Double minPrice, Double maxPrice, Boolean active, Pageable pageable) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("minPrice não pode ser maior que maxPrice");
        }

        Specification<Product> spec = Specification.where(null);

        if (StringUtils.hasText(name)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%")
            );
        }

        if (StringUtils.hasText(category)) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase())
            );
        }

        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        if (active != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), active));
        }

        return productRepository.findAll(spec, pageable).map(ProductService::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductService.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = ProductService.toEntity(request);
        product.setId(null);
        return ProductService.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setCategory(request.getCategory());
        existing.setStockQuantity(request.getStockQuantity());
        existing.setActive(Boolean.TRUE.equals(request.getActive()));

        return ProductService.toResponse(productRepository.save(existing));
    }

    @Override
    @Transactional
    public ProductResponse adjustStock(Long id, int delta) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        int projectedStock = product.getStockQuantity() + delta;
        if (projectedStock < 0) {
            throw new InsufficientStockException(id, product.getStockQuantity(), delta);
        }

        product.setStockQuantity(projectedStock);
        return ProductService.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(existing);
    }
}
