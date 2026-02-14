package com.Captando.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comandas")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    @Enumerated(EnumType.STRING)
    private ComandaStatus status = ComandaStatus.ABERTA;
    @ManyToOne
    private Customer customer;

    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Double discountPercent = 0.0;
    private Double discountAmount = 0.0;
    private PaymentMethod paymentMethod;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComandaItem> items = new ArrayList<>();

    public Comanda() {
    }

    public Comanda(String customerName) {
        this.customerName = customerName;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public ComandaStatus getStatus() {
        return status;
    }

    public void setStatus(ComandaStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent == null ? 0.0 : discountPercent;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount == null ? 0.0 : discountAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<ComandaItem> getItems() {
        return items;
    }

    public void setItems(List<ComandaItem> items) {
        this.items = items;
    }

    public void addItem(ComandaItem item) {
        item.setComanda(this);
        this.items.add(item);
    }

    public void removeItem(ComandaItem item) {
        this.items.remove(item);
        item.setComanda(null);
    }

    public double getTotal() {
        double sub = items.stream()
                .mapToDouble(ComandaItem::getLineTotal)
                .sum();
        double withPercentageDiscount = sub - (sub * ((discountPercent == null ? 0.0 : discountPercent) / 100.0));
        return Math.max(0.0, withPercentageDiscount - (discountAmount == null ? 0.0 : discountAmount));
    }

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(ComandaItem::getLineTotal)
                .sum();
    }
}
