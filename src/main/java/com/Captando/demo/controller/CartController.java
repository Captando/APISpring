package com.Captando.demo.controller;

import com.Captando.demo.dto.AddComandaItemRequest;
import com.Captando.demo.dto.ComandaCheckoutRequest;
import com.Captando.demo.dto.ComandaResponse;
import com.Captando.demo.dto.CreateComandaRequest;
import com.Captando.demo.service.ComandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@Tag(name = "Carrinho", description = "Carrinho de compra (alias da comanda)")
public class CartController {

    private final ComandaService comandaService;

    public CartController(ComandaService comandaService) {
        this.comandaService = comandaService;
    }

    @GetMapping
    @Operation(summary = "Listar carrinhos")
    public Page<ComandaResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        String[] sortParts = sort.split(",");
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortParts[1].trim()), sortParts[0].trim()));
        return comandaService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar carrinho por id")
    public ComandaResponse get(@PathVariable Long id) {
        return comandaService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Abrir carrinho")
    public ComandaResponse create(@RequestBody CreateComandaRequest request) {
        return comandaService.create(request);
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Adicionar item ao carrinho")
    public ComandaResponse addItem(@PathVariable Long id, @RequestBody AddComandaItemRequest request) {
        return comandaService.addItem(id, request);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Remover item do carrinho")
    public ComandaResponse removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        return comandaService.removeItem(id, itemId);
    }

    @PatchMapping("/{id}/checkout")
    @Operation(summary = "Checkout do carrinho")
    public ComandaResponse checkout(@PathVariable Long id, @RequestBody ComandaCheckoutRequest request) {
        return comandaService.checkout(id, request);
    }
}

