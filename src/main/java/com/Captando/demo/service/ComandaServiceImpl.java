package com.Captando.demo.service;

import com.Captando.demo.dto.AddComandaItemRequest;
import com.Captando.demo.dto.ApplyComandaDiscountRequest;
import com.Captando.demo.dto.ComandaCheckoutRequest;
import com.Captando.demo.dto.ComandaItemResponse;
import com.Captando.demo.dto.ComandaResponse;
import com.Captando.demo.dto.CreateComandaRequest;
import com.Captando.demo.model.Comanda;
import com.Captando.demo.model.ComandaItem;
import com.Captando.demo.model.ComandaStatus;
import com.Captando.demo.model.Customer;
import com.Captando.demo.model.PaymentMethod;
import com.Captando.demo.model.Product;
import com.Captando.demo.repository.ComandaRepository;
import com.Captando.demo.repository.CustomerRepository;
import com.Captando.demo.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComandaServiceImpl implements ComandaService {

    private final ComandaRepository comandaRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public ComandaServiceImpl(ComandaRepository comandaRepository,
                              ProductRepository productRepository,
                              CustomerRepository customerRepository) {
        this.comandaRepository = comandaRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComandaResponse> findAll(Pageable pageable) {
        return comandaRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ComandaResponse findById(Long id) {
        return toResponse(comandaRepository.findById(id)
                .orElseThrow(() -> new ComandaNotFoundException(id)));
    }

    @Override
    @Transactional
    public ComandaResponse create(CreateComandaRequest request) {
        Comanda comanda = new Comanda(request.getCustomerName());
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
            comanda.setCustomer(customer);
        }
        return toResponse(comandaRepository.save(comanda));
    }

    @Override
    @Transactional
    public ComandaResponse addItem(Long id, AddComandaItemRequest request) {
        Comanda comanda = findOpenComanda(id);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        List<ComandaItem> existing = comanda.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .toList();

        if (!existing.isEmpty()) {
            ComandaItem item = existing.get(0);
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            ComandaItem item = new ComandaItem(product, request.getQuantity(), product.getPrice());
            comanda.addItem(item);
        }
        return toResponse(comandaRepository.save(comanda));
    }

    @Override
    @Transactional
    public ComandaResponse removeItem(Long comandaId, Long itemId) {
        Comanda comanda = findOpenComanda(comandaId);
        ComandaItem item = comanda.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado na comanda: " + itemId));
        comanda.removeItem(item);
        return toResponse(comandaRepository.save(comanda));
    }

    @Override
    @Transactional
    public ComandaResponse applyDiscount(Long id, ApplyComandaDiscountRequest request) {
        Comanda comanda = findOpenComanda(id);
        comanda.setDiscountPercent(request.getDiscountPercent());
        comanda.setDiscountAmount(request.getDiscountAmount());
        return toResponse(comandaRepository.save(comanda));
    }

    @Override
    @Transactional
    public ComandaResponse checkout(Long id, ComandaCheckoutRequest request) {
        Comanda comanda = findOpenComanda(id);
        comanda.setPaymentMethod(request.getPaymentMethod());
        comanda.setStatus(ComandaStatus.FECHADA);
        comanda.setClosedAt(LocalDateTime.now());
        return toResponse(comandaRepository.save(comanda));
    }

    @Override
    @Transactional
    public ComandaResponse close(Long id) {
        Comanda comanda = findOpenComanda(id);
        if (comanda.getPaymentMethod() == null) {
            comanda.setPaymentMethod(PaymentMethod.CASH);
        }
        comanda.setStatus(ComandaStatus.FECHADA);
        comanda.setClosedAt(LocalDateTime.now());
        return toResponse(comandaRepository.save(comanda));
    }

    @Override
    @Transactional
    public ComandaResponse setPaymentMethod(Long id, PaymentMethod paymentMethod) {
        Comanda comanda = findOpenComanda(id);
        comanda.setPaymentMethod(paymentMethod);
        return toResponse(comandaRepository.save(comanda));
    }

    @Override
    public List<String> availablePaymentMethods() {
        return List.of(PaymentMethod.values())
                .stream().map(Enum::name).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new ComandaNotFoundException(id));
        comandaRepository.delete(comanda);
    }

    private Comanda findOpenComanda(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new ComandaNotFoundException(id));
        ensureOpen(comanda);
        return comanda;
    }

    private void ensureOpen(Comanda comanda) {
        if (comanda.getStatus() != ComandaStatus.ABERTA) {
            throw new ComandaClosedException(comanda.getId());
        }
    }

    private ComandaResponse toResponse(Comanda comanda) {
        List<ComandaItemResponse> itemDtos = comanda.getItems().stream()
                .map(item -> new ComandaItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                ))
                .toList();

        return new ComandaResponse(
                comanda.getId(),
                comanda.getCustomerName(),
                comanda.getCustomer() != null ? comanda.getCustomer().getId() : null,
                comanda.getStatus().name(),
                comanda.getCreatedAt(),
                comanda.getClosedAt(),
                comanda.getSubtotal(),
                comanda.getDiscountPercent(),
                comanda.getDiscountAmount(),
                comanda.getTotal(),
                comanda.getPaymentMethod() != null ? comanda.getPaymentMethod().name() : null,
                itemDtos
        );
    }
}

