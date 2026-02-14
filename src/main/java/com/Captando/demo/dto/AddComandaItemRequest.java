package com.Captando.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddComandaItemRequest {

    @NotNull(message = "productId é obrigatório")
    private Long productId;

    @NotNull(message = "quantity é obrigatório")
    @Min(value = 1, message = "quantity deve ser pelo menos 1")
    private Integer quantity;

    public AddComandaItemRequest() {
    }

    public AddComandaItemRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}

