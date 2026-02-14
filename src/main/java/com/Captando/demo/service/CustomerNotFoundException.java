package com.Captando.demo.service;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("Cliente não encontrado com id: " + id);
    }
}

