package com.Captando.demo.service;

import com.Captando.demo.dto.AddComandaItemRequest;
import com.Captando.demo.dto.ComandaResponse;
import com.Captando.demo.dto.CreateComandaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComandaService {
    Page<ComandaResponse> findAll(Pageable pageable);
    ComandaResponse findById(Long id);
    ComandaResponse create(CreateComandaRequest request);
    ComandaResponse addItem(Long id, AddComandaItemRequest request);
    ComandaResponse removeItem(Long comandaId, Long itemId);
    ComandaResponse close(Long id);
    void delete(Long id);
}

