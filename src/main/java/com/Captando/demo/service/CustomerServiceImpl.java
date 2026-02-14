package com.Captando.demo.service;

import com.Captando.demo.dto.CustomerRequest;
import com.Captando.demo.dto.CustomerResponse;
import com.Captando.demo.model.Customer;
import com.Captando.demo.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(CustomerService::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return CustomerService.toResponse(getOrElseThrow(id));
    }

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        Customer customer = CustomerService.toEntity(request);
        return CustomerService.toResponse(repository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer existing = getOrElseThrow(id);
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        return CustomerService.toResponse(repository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer existing = getOrElseThrow(id);
        repository.delete(existing);
    }

    private Customer getOrElseThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }
}

