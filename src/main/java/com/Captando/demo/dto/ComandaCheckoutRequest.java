package com.Captando.demo.dto;

import com.Captando.demo.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class ComandaCheckoutRequest {

    @NotNull(message = "paymentMethod é obrigatório")
    private PaymentMethod paymentMethod;

    public ComandaCheckoutRequest() {
    }

    public ComandaCheckoutRequest(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}

