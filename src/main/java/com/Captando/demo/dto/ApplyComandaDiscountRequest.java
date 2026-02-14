package com.Captando.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public class ApplyComandaDiscountRequest {

    @Min(value = 0, message = "discountPercent não pode ser menor que 0")
    @Max(value = 100, message = "discountPercent não pode ser maior que 100")
    private Double discountPercent = 0.0;

    @PositiveOrZero(message = "discountAmount não pode ser negativo")
    private Double discountAmount = 0.0;

    public ApplyComandaDiscountRequest() {
    }

    public ApplyComandaDiscountRequest(Double discountPercent, Double discountAmount) {
        this.discountPercent = discountPercent == null ? 0.0 : discountPercent;
        this.discountAmount = discountAmount == null ? 0.0 : discountAmount;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }
}

