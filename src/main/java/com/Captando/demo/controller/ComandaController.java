package com.Captando.demo.controller;

import com.Captando.demo.dto.AddComandaItemRequest;
import com.Captando.demo.dto.ComandaResponse;
import com.Captando.demo.dto.CreateComandaRequest;
import com.Captando.demo.service.ComandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comandas")
@Tag(name = "Comandas", description = "Controle de comanda para vendas no mercado")
public class ComandaController {

    private final ComandaService comandaService;

    public ComandaController(ComandaService comandaService) {
        this.comandaService = comandaService;
    }

    @GetMapping
    @Operation(summary = "Listar comandas")
    public Page<ComandaResponse> getAllComandas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        String[] sortParts = sort.split(",");
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortParts[1].trim()), sortParts[0].trim()));

        return comandaService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar comanda por id")
    public ComandaResponse getById(@PathVariable Long id) {
        return comandaService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Criar comanda")
    public ResponseEntity<ComandaResponse> create(@Valid @RequestBody CreateComandaRequest request) {
        ComandaResponse created = comandaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Adicionar item na comanda")
    public ComandaResponse addItem(
            @PathVariable Long id,
            @Valid @RequestBody AddComandaItemRequest request) {
        return comandaService.addItem(id, request);
    }

    @DeleteMapping("/{comandaId}/items/{itemId}")
    @Operation(summary = "Remover item da comanda")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long comandaId, @PathVariable Long itemId) {
        comandaService.removeItem(comandaId, itemId);
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Fechar comanda")
    public ComandaResponse close(@PathVariable Long id) {
        return comandaService.close(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover comanda")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        comandaService.delete(id);
    }
}

