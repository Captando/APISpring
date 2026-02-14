package com.Captando.demo.controller;

import com.Captando.demo.dto.ProductRequest;
import com.Captando.demo.dto.ProductResponse;
import com.Captando.demo.dto.StockAdjustmentRequest;
import com.Captando.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/products", "/api/v1/products"})
@Tag(name = "Produtos", description = "Catálogo de produtos do mercado")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Listar produtos")
    public Page<ProductResponse> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        String[] sortParts = sort.split(",");
        Pageable pageable;
        if (sortParts.length == 2) {
            String direction = sortParts[1].trim().equalsIgnoreCase("desc") ? "desc" : "asc";
            pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(Sort.Direction.fromString(direction), sortParts[0].trim())
            );
        } else {
            pageable = PageRequest.of(page, size);
        }
        return productService.findAll(name, category, minPrice, maxPrice, active, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por id")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Criar novo produto")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Ajustar estoque")
    public ProductResponse adjustStock(
            @PathVariable Long id,
            @Valid @RequestBody StockAdjustmentRequest request) {
        return productService.adjustStock(id, request.getDelta());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover produto")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }
}
