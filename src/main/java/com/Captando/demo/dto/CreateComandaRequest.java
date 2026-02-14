package com.Captando.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateComandaRequest {

    @NotBlank(message = "customerName é obrigatório")
    private String customerName;
    private Long customerId;

    public CreateComandaRequest() {
    }

    public CreateComandaRequest(String customerName, Long customerId) {
        this.customerName = customerName;
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
