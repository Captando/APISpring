package com.Captando.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductRequest {

    @NotBlank(message = "name é obrigatório")
    @Size(min = 2, max = 120, message = "name deve ter entre 2 e 120 caracteres")
    private String name;

    @Size(max = 500, message = "description deve ter no máximo 500 caracteres")
    private String description;

    @NotNull(message = "price é obrigatório")
    @Positive(message = "price deve ser maior que zero")
    private Double price;

    @Size(max = 80, message = "category deve ter no máximo 80 caracteres")
    private String category;

    @NotNull(message = "stockQuantity é obrigatório")
    @Min(value = 0, message = "stockQuantity não pode ser menor que 0")
    private Integer stockQuantity;

    private Boolean active = true;

    public ProductRequest() {
    }

    public ProductRequest(String name, String description, Double price, String category, Integer stockQuantity, Boolean active) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.active = active == null ? true : active;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public Boolean getActive() {
        return active;
    }
}
