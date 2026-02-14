package com.Captando.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComandaResponse {
    private Long id;
    private String customerName;
    private Long customerId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Double subtotal;
    private Double discountPercent;
    private Double discountAmount;
    private Double total;
    private String paymentMethod;
    private List<ComandaItemResponse> items = new ArrayList<>();

    public ComandaResponse() {
    }

    public ComandaResponse(Long id, String customerName, Long customerId, String status,
                           LocalDateTime createdAt, LocalDateTime closedAt,
                           Double subtotal, Double discountPercent, Double discountAmount,
                           Double total, String paymentMethod, List<ComandaItemResponse> items) {
        this.id = id;
        this.customerName = customerName;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.subtotal = subtotal;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getCustomerId() {
        return customerId;
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

    public Double getSubtotal() {
        return subtotal;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public Double getTotal() {
        return total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public List<ComandaItemResponse> getItems() {
        return items;
    }
}
