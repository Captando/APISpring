package com.Captando.demo.dto;

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

    public ProductRequest() {
    }

    public ProductRequest(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
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
}
