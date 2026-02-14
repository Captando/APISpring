package com.Captando.demo.dto;

public class ComandaItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;

    public ComandaItemResponse() {
    }

    public ComandaItemResponse(Long id, Long productId, String productName, Integer quantity, Double unitPrice, Double lineTotal) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public Double getLineTotal() {
        return lineTotal;
    }
}

