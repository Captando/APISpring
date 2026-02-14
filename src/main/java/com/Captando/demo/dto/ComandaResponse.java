package com.Captando.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComandaResponse {
    private Long id;
    private String customerName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Double total;
    private List<ComandaItemResponse> items = new ArrayList<>();

    public ComandaResponse() {
    }

    public ComandaResponse(Long id, String customerName, String status, LocalDateTime createdAt, LocalDateTime closedAt, Double total, List<ComandaItemResponse> items) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.total = total;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public Double getTotal() {
        return total;
    }

    public List<ComandaItemResponse> getItems() {
        return items;
    }
}

