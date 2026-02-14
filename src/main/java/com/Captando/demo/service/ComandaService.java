package com.Captando.demo.service;

import com.Captando.demo.dto.AddComandaItemRequest;
import com.Captando.demo.dto.ApplyComandaDiscountRequest;
import com.Captando.demo.dto.ComandaCheckoutRequest;
import com.Captando.demo.dto.ComandaResponse;
import com.Captando.demo.dto.CreateComandaRequest;
import com.Captando.demo.model.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ComandaService {
    Page<ComandaResponse> findAll(Pageable pageable);
    ComandaResponse findById(Long id);
    ComandaResponse create(CreateComandaRequest request);
    ComandaResponse addItem(Long id, AddComandaItemRequest request);
    ComandaResponse removeItem(Long comandaId, Long itemId);
    ComandaResponse applyDiscount(Long id, ApplyComandaDiscountRequest request);
    ComandaResponse checkout(Long id, ComandaCheckoutRequest request);
    ComandaResponse close(Long id);
    ComandaResponse setPaymentMethod(Long id, PaymentMethod paymentMethod);
    List<String> availablePaymentMethods();
    void delete(Long id);
}

