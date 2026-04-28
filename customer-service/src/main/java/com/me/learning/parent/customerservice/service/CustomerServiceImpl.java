package com.me.learning.parent.customerservice.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.customerservice.dto.CustomerDetailResponse;
import com.me.learning.parent.customerservice.dto.CustomerRequest;
import com.me.learning.parent.customerservice.dto.CustomerResponse;
import com.me.learning.parent.customerservice.dto.CustomerUpdateRequest;
import com.me.learning.parent.customerservice.entity.Customer;
import com.me.learning.parent.customerservice.mapper.CustomerMapper;
import com.me.learning.parent.customerservice.repository.AddressRepository;
import com.me.learning.parent.customerservice.repository.CustomerRepository;


/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Service implementation for Customer entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private static final String CUSTOMER_RESOURCE = "Customer";
    private static final String ADDRESS_RESOURCE = "Address";
    private static final String STORE_RESOURCE = "Store";
    private static final String FIELD_ID = "id";
    private static final String FIELD_EMAIL = "email";

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    @CacheEvict (value = {"customers", "customer"}, allEntries = true)
    public CustomerResponse createCustomer (CustomerRequest request) {
        log.debug ("Creating new customer: {} {}", request.firstName (), request.lastName ());

        if ( request.email () != null && customerRepository.existsByEmailIgnoreCase (request.email ()) ) {
            throw new DuplicateResourceException (CUSTOMER_RESOURCE, FIELD_EMAIL, request.email ());
        }
        if ( !addressRepository.existsById (request.addressId ()) ) {
            throw new ResourceNotFoundException (ADDRESS_RESOURCE, FIELD_ID, request.addressId ());
        }

        Customer customer = customerMapper.toEntity (request);
        Customer saved = customerRepository.save (customer);

        log.info ("Created customer with ID: {}", saved.getId ());
        return customerMapper.toResponse (saved);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"customers", "customer"}, allEntries = true)
    public CustomerResponse updateCustomer (Integer id, CustomerUpdateRequest request) {
        log.debug ("Updating customer with ID: {}", id);

        Customer existing = customerRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (CUSTOMER_RESOURCE, FIELD_ID, id));

        if ( request.email () != null &&
                !request.email ().equalsIgnoreCase (existing.getEmail ()) &&
                customerRepository.existsByEmailIgnoreCase (request.email ()) ) {
            throw new DuplicateResourceException (CUSTOMER_RESOURCE, FIELD_EMAIL, request.email ());
        }

        existing.setFirstName (request.firstName ());
        existing.setLastName (request.lastName ());
        existing.setEmail (request.email ());
        existing.setActive (request.active ());
        existing.getAddress ().setId (request.addressId ());

        Customer updated = customerRepository.save (existing);

        log.info ("Updated customer with ID: {}", id);
        return customerMapper.toResponse (updated);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"customers", "customer"}, allEntries = true)
    public CustomerResponse patchCustomer (Integer id, CustomerUpdateRequest request) {
        log.debug ("Patching customer with ID: {}", id);

        Customer existing = customerRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (CUSTOMER_RESOURCE, FIELD_ID, id));

        customerMapper.updateEntity (request, existing);
        Customer updated = customerRepository.save (existing);

        log.info ("Patched customer with ID: {}", id);
        return customerMapper.toResponse (updated);
    }

    @Override
    public CustomerDetailResponse getCustomerById (Integer id) {
        log.debug ("Fetching customer with ID: {}", id);

        Customer customer = customerRepository.findByIdWithDetails (id)
                .orElseThrow (() -> new ResourceNotFoundException (CUSTOMER_RESOURCE, FIELD_ID, id));

        return customerMapper.toDetailResponse (customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers () {
        log.debug ("Fetching all customers");
        return customerMapper.toResponseList (customerRepository.findAll ());
    }

    @Override
    public Page<CustomerResponse> getAllCustomers (Pageable pageable) {
        log.debug ("Fetching customers â€” page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return customerRepository.findAll (pageable).map (customerMapper::toResponse);
    }

    @Override
    public List<CustomerResponse> getActiveCustomers () {
        log.debug ("Fetching all active customers");
        return customerMapper.toResponseList (customerRepository.findByActiveTrue ());
    }

    @Override
    public List<CustomerResponse> getCustomersByStoreId (Short storeId) {
        log.debug ("Fetching customers for store ID: {}", storeId);
        return customerMapper.toResponseList (customerRepository.findByStoreId (storeId));
    }

    @Override
    public List<CustomerResponse> searchByLastName (String lastName) {
        log.debug ("Searching customers by last name: {}", lastName);
        return customerMapper.toResponseList (
                customerRepository.findByLastNameIgnoreCaseContaining (lastName));
    }

    @Override
    @Transactional
    @CacheEvict (value = {"customers", "customer"}, allEntries = true)
    public void deleteCustomer (Integer id) {
        log.debug ("Deleting customer with ID: {}", id);

        if ( !customerRepository.existsById (id) ) {
            throw new ResourceNotFoundException (CUSTOMER_RESOURCE, FIELD_ID, id);
        }

        customerRepository.deleteById (id);
        log.info ("Deleted customer with ID: {}", id);
    }

    @Override
    public boolean existsById (Integer id) {
        return customerRepository.existsById (id);
    }

    @Override
    public long countCustomers () {
        return customerRepository.count ();
    }
}

