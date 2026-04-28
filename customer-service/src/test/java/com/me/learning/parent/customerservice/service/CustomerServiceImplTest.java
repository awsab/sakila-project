package com.me.learning.parent.customerservice.service;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.customerservice.dto.AddressResponse;
import com.me.learning.parent.customerservice.dto.CustomerRequest;
import com.me.learning.parent.customerservice.dto.CustomerDetailResponse;
import com.me.learning.parent.customerservice.dto.CustomerResponse;
import com.me.learning.parent.customerservice.dto.CustomerUpdateRequest;
import com.me.learning.parent.customerservice.dto.PaymentDetailResponse;
import com.me.learning.parent.customerservice.entity.Address;
import com.me.learning.parent.customerservice.entity.Customer;
import com.me.learning.parent.customerservice.mapper.CustomerMapper;
import com.me.learning.parent.customerservice.repository.AddressRepository;
import com.me.learning.parent.customerservice.repository.CustomerRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author   : Prabakaran Ramu
 * Date     : 23/04/2026
 * Usage    : Unit tests for CustomerServiceImpl
 */
@ExtendWith (MockitoExtension.class)
@DisplayName ("CustomerServiceImpl")
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerResponse customerResponse;
    private CustomerDetailResponse customerDetailResponse;
    private CustomerRequest customerRequest;
    private CustomerUpdateRequest customerUpdateRequest;

    @BeforeEach
    void setUp () {
        Address address = new Address ();
        address.setId (1);

        customer = new Customer ();
        customer.setId (1);
        customer.setFirstName ("MARY");
        customer.setLastName ("SMITH");
        customer.setEmail ("mary.smith@sakilacustomer.org");
        customer.setAddress (address);
        customer.setActive (true);
        customer.setCreateDate (Instant.now ());

        customerResponse = new CustomerResponse (
                1, (short) 1, "MARY", "SMITH",
                "mary.smith@sakilacustomer.org", 1, true,
                Instant.now (), Instant.now ()
        );

        AddressResponse addressResponse = new AddressResponse (
                1, "1 Main St", null, "District", 2, "City", "12345", "1234567890", Instant.now ()
        );
        PaymentDetailResponse paymentDetailResponse = new PaymentDetailResponse (
                10, (short) 3, new BigDecimal ("12.34"), Instant.now (), Instant.now ()
        );
        customerDetailResponse = new CustomerDetailResponse (
                1, (short) 1, "MARY", "SMITH",
                "mary.smith@sakilacustomer.org", 1, addressResponse,
                List.of (paymentDetailResponse), true,
                Instant.now (), Instant.now ()
        );

        customerRequest = new CustomerRequest (
                (short) 1, "MARY", "SMITH",
                "mary.smith@sakilacustomer.org", 1, true, Instant.now ()
        );

        customerUpdateRequest = new CustomerUpdateRequest (
                (short) 1, "MARY", "SMITH",
                "mary.smith@sakilacustomer.org", 1, true
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("createCustomer")
    class CreateCustomer {

        @Test
        @DisplayName ("should create and return customer when email is unique and address exists")
        void createCustomer_Success () {
            when (customerRepository.existsByEmailIgnoreCase (customerRequest.email ())).thenReturn (false);
            when (addressRepository.existsById (customerRequest.addressId ())).thenReturn (true);
            when (customerMapper.toEntity (customerRequest)).thenReturn (customer);
            when (customerRepository.save (customer)).thenReturn (customer);
            when (customerMapper.toResponse (customer)).thenReturn (customerResponse);

            CustomerResponse result = customerService.createCustomer (customerRequest);

            assertThat (result).isNotNull ();
            assertThat (result.firstName ()).isEqualTo ("MARY");
            verify (customerRepository).save (customer);
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when email already exists")
        void createCustomer_DuplicateEmail () {
            when (customerRepository.existsByEmailIgnoreCase (customerRequest.email ())).thenReturn (true);

            assertThatThrownBy (() -> customerService.createCustomer (customerRequest))
                    .isInstanceOf (DuplicateResourceException.class);

            verify (customerRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when address does not exist")
        void createCustomer_AddressNotFound () {
            when (customerRepository.existsByEmailIgnoreCase (customerRequest.email ())).thenReturn (false);
            when (addressRepository.existsById (customerRequest.addressId ())).thenReturn (false);

            assertThatThrownBy (() -> customerService.createCustomer (customerRequest))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (customerRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("updateCustomer")
    class UpdateCustomer {

        @Test
        @DisplayName ("should update and return customer when ID exists and email is unique")
        void updateCustomer_Success () {
            when (customerRepository.findById (1)).thenReturn (Optional.of (customer));
            when (customerRepository.save (customer)).thenReturn (customer);
            when (customerMapper.toResponse (customer)).thenReturn (customerResponse);

            CustomerResponse result = customerService.updateCustomer (1, customerUpdateRequest);

            assertThat (result).isNotNull ();
            verify (customerRepository).save (customer);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when customer ID does not exist")
        void updateCustomer_NotFound () {
            when (customerRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> customerService.updateCustomer (99, customerUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when new email belongs to another customer")
        void updateCustomer_DuplicateEmail () {
            Customer otherCustomer = new Customer ();
            otherCustomer.setId (1);
            otherCustomer.setEmail ("other@example.com");
            Address address = new Address ();
            address.setId (2);
            otherCustomer.setAddress (address);

            CustomerUpdateRequest requestWithNewEmail = new CustomerUpdateRequest (
                    (short) 1, "MARY", "SMITH", "taken@example.com", 1, true
            );

            when (customerRepository.findById (1)).thenReturn (Optional.of (otherCustomer));
            when (customerRepository.existsByEmailIgnoreCase ("taken@example.com")).thenReturn (true);

            assertThatThrownBy (() -> customerService.updateCustomer (1, requestWithNewEmail))
                    .isInstanceOf (DuplicateResourceException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("patchCustomer")
    class PatchCustomer {

        @Test
        @DisplayName ("should patch and return customer when ID exists")
        void patchCustomer_Success () {
            when (customerRepository.findById (1)).thenReturn (Optional.of (customer));
            when (customerRepository.save (customer)).thenReturn (customer);
            when (customerMapper.toResponse (customer)).thenReturn (customerResponse);

            CustomerResponse result = customerService.patchCustomer (1, customerUpdateRequest);

            assertThat (result).isNotNull ();
            verify (customerMapper).updateEntity (customerUpdateRequest, customer);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when customer ID does not exist")
        void patchCustomer_NotFound () {
            when (customerRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> customerService.patchCustomer (99, customerUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getCustomerById")
    class GetCustomerById {

        @Test
        @DisplayName ("should return customer when ID exists")
        void getCustomerById_Success () {
            when (customerRepository.findByIdWithDetails (1)).thenReturn (Optional.of (customer));
            when (customerMapper.toDetailResponse (customer)).thenReturn (customerDetailResponse);

            CustomerDetailResponse result = customerService.getCustomerById (1);

            assertThat (result).isNotNull ();
            assertThat (result.id ()).isEqualTo (1);
            assertThat (result.address ().id ()).isEqualTo (1);
            assertThat (result.payments ()).hasSize (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void getCustomerById_NotFound () {
            when (customerRepository.findByIdWithDetails (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> customerService.getCustomerById (99))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getAllCustomers")
    class GetAllCustomers {

        @Test
        @DisplayName ("should return all customers as a list")
        void getAllCustomers_List () {
            when (customerRepository.findAll ()).thenReturn (List.of (customer));
            when (customerMapper.toResponseList (List.of (customer))).thenReturn (List.of (customerResponse));

            List<CustomerResponse> result = customerService.getAllCustomers ();

            assertThat (result).hasSize (1);
        }

        @Test
        @DisplayName ("should return paginated customers")
        void getAllCustomers_Paginated () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Customer> page = new PageImpl<> (List.of (customer));
            when (customerRepository.findAll (pageable)).thenReturn (page);
            when (customerMapper.toResponse (customer)).thenReturn (customerResponse);

            Page<CustomerResponse> result = customerService.getAllCustomers (pageable);

            assertThat (result.getContent ()).hasSize (1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getActiveCustomers")
    class GetActiveCustomers {

        @Test
        @DisplayName ("should return only active customers")
        void getActiveCustomers_Success () {
            when (customerRepository.findByActiveTrue ()).thenReturn (List.of (customer));
            when (customerMapper.toResponseList (List.of (customer))).thenReturn (List.of (customerResponse));

            List<CustomerResponse> result = customerService.getActiveCustomers ();

            assertThat (result).hasSize (1);
            assertThat (result.getFirst ().active ()).isTrue ();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getCustomersByStoreId")
    class GetCustomersByStoreId {

        @Test
        @DisplayName ("should return customers belonging to the given store")
        void getCustomersByStoreId_Success () {
            when (customerRepository.findByStoreId ((short) 1)).thenReturn (List.of (customer));
            when (customerMapper.toResponseList (List.of (customer))).thenReturn (List.of (customerResponse));

            List<CustomerResponse> result = customerService.getCustomersByStoreId ((short) 1);

            assertThat (result).hasSize (1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("searchByLastName")
    class SearchByLastName {

        @Test
        @DisplayName ("should return customers matching the last name")
        void searchByLastName_Success () {
            when (customerRepository.findByLastNameIgnoreCaseContaining ("SMITH"))
                    .thenReturn (List.of (customer));
            when (customerMapper.toResponseList (List.of (customer))).thenReturn (List.of (customerResponse));

            List<CustomerResponse> result = customerService.searchByLastName ("SMITH");

            assertThat (result).hasSize (1);
            assertThat (result.getFirst ().lastName ()).isEqualTo ("SMITH");
        }

        @Test
        @DisplayName ("should return empty list when no match found")
        void searchByLastName_Empty () {
            when (customerRepository.findByLastNameIgnoreCaseContaining ("UNKNOWN"))
                    .thenReturn (List.of ());
            when (customerMapper.toResponseList (List.of ())).thenReturn (List.of ());

            List<CustomerResponse> result = customerService.searchByLastName ("UNKNOWN");

            assertThat (result).isEmpty ();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("deleteCustomer")
    class DeleteCustomer {

        @Test
        @DisplayName ("should delete customer when ID exists")
        void deleteCustomer_Success () {
            when (customerRepository.existsById (1)).thenReturn (true);

            customerService.deleteCustomer (1);

            verify (customerRepository).deleteById (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void deleteCustomer_NotFound () {
            when (customerRepository.existsById (99)).thenReturn (false);

            assertThatThrownBy (() -> customerService.deleteCustomer (99))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (customerRepository, never ()).deleteById (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("existsById")
    class ExistsById {

        @Test
        @DisplayName ("should return true when customer exists")
        void existsById_True () {
            when (customerRepository.existsById (1)).thenReturn (true);
            assertThat (customerService.existsById (1)).isTrue ();
        }

        @Test
        @DisplayName ("should return false when customer does not exist")
        void existsById_False () {
            when (customerRepository.existsById (99)).thenReturn (false);
            assertThat (customerService.existsById (99)).isFalse ();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("countCustomers")
    class CountCustomers {

        @Test
        @DisplayName ("should return total customer count")
        void countCustomers_Success () {
            when (customerRepository.count ()).thenReturn (42L);
            assertThat (customerService.countCustomers ()).isEqualTo (42L);
        }
    }
}

