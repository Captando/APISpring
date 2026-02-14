package com.Captando.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {

    @NotNull(message = "delta é obrigatório")
    @Min(value = -9999, message = "delta não pode ser menor que -9999")
    private Integer delta;

    public StockAdjustmentRequest() {
    }

    public StockAdjustmentRequest(Integer delta) {
        this.delta = delta;
    }

    public Integer getDelta() {
        return delta;
    }
}
