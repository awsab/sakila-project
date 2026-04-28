package com.me.learning.parent.customerservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.customerservice.dto.CustomerDetailResponse;
import com.me.learning.parent.customerservice.dto.CustomerRequest;
import com.me.learning.parent.customerservice.dto.CustomerResponse;
import com.me.learning.parent.customerservice.dto.CustomerUpdateRequest;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Service interface for Customer entity operations
 * Since    : Version 1.0
 */
public interface CustomerService {

    /** Create a new customer. */
    CustomerResponse createCustomer (CustomerRequest request);

    /** Fully update an existing customer. */
    CustomerResponse updateCustomer (Integer id, CustomerUpdateRequest request);

    /** Partially update an existing customer. */
    CustomerResponse patchCustomer (Integer id, CustomerUpdateRequest request);

    /** Retrieve a customer by its ID. */
    CustomerDetailResponse getCustomerById (Integer id);

    /** Retrieve all customers. */
    List<CustomerResponse> getAllCustomers ();

    /** Retrieve all customers with pagination. */
    Page<CustomerResponse> getAllCustomers (Pageable pageable);

    /** Retrieve all active customers. */
    List<CustomerResponse> getActiveCustomers ();

    /** Retrieve all customers belonging to a store. */
    List<CustomerResponse> getCustomersByStoreId (Short storeId);

    /** Search customers by last name. */
    List<CustomerResponse> searchByLastName (String lastName);

    /** Delete a customer by ID. */
    void deleteCustomer (Integer id);

    /** Check whether a customer exists by ID. */
    boolean existsById (Integer id);

    /** Count total customers. */
    long countCustomers ();
}

