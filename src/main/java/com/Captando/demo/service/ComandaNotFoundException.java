package com.Captando.demo.service;

public class ComandaNotFoundException extends RuntimeException {
    public ComandaNotFoundException(Long id) {
        super("Comanda não encontrada com id: " + id);
    }
}

