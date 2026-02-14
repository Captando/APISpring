package com.Captando.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateComandaRequest {

    @NotBlank(message = "customerName é obrigatório")
    private String customerName;

    public CreateComandaRequest() {
    }

    public CreateComandaRequest(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }
}

