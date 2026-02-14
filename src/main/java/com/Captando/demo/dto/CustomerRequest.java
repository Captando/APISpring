package com.Captando.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class CustomerRequest {

    @NotBlank(message = "name é obrigatório")
    private String name;

    private String email;
    private String phone;

    public CustomerRequest() {
    }

    public CustomerRequest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}

