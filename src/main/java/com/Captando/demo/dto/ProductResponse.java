package com.Captando.demo.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Integer stockQuantity;
    private boolean active;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name, String description, Double price, String category, Integer stockQuantity, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public boolean isActive() {
        return active;
    }
}
