package com.Captando.demo.service;

public class ComandaClosedException extends IllegalStateException {
    public ComandaClosedException(Long id) {
        super("Comanda não pode ser alterada pois está fechada. id: " + id);
    }
}

