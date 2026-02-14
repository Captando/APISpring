package com.Captando.demo.service;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long id, int available, int requested) {
        super("Estoque insuficiente para o produto id " + id + ". Disponível: " + available + ", solicitado: " + requested);
    }
}
