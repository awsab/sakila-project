package com.me.learning.rental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.me.learning.framework.web.errors.BadRequestException;
import com.me.learning.rental.dto.PaymentRequestDto;
import com.me.learning.rental.dto.PaymentResponseDto;
import com.me.learning.rental.dto.RentalCheckoutRequestDto;
import com.me.learning.rental.dto.RentalCheckoutResponseDto;
import com.me.learning.rental.dto.RentalRequestDto;
import com.me.learning.rental.dto.RentalResponseDto;
import com.me.learning.rental.entity.Payment;
import com.me.learning.rental.entity.Rental;
import com.me.learning.rental.mapper.PaymentMapper;
import com.me.learning.rental.mapper.RentalMapper;
import com.me.learning.rental.repository.PaymentRepository;
import com.me.learning.rental.repository.RentalRepository;

/**
 * Focused unit tests for the Sakila checkout flow in {@link RentalServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RentalServiceImpl checkout()")
@SuppressWarnings("PMD.MethodNamingConventions")
class RentalCheckoutServiceImplTest {

    private static final Instant NOW = Instant.parse("2024-06-01T12:00:00Z");
    private static final int INVENTORY_ID = 1;
    private static final int CUSTOMER_ID = 8;
    private static final short STAFF_ID = 2;
    private static final BigDecimal AMOUNT = new BigDecimal("4.99");

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private RentalRequestDto buildRentalRequest () {
        return new RentalRequestDto(null, NOW, INVENTORY_ID, CUSTOMER_ID, null, STAFF_ID, NOW);
    }

    private PaymentRequestDto buildPaymentRequest () {
        return new PaymentRequestDto(null, CUSTOMER_ID, STAFF_ID, null, AMOUNT, NOW, NOW);
    }

    @Test
    @DisplayName("should create rental and payment in a single transaction")
    void checkout_withMatchingCustomerAndStaff_returnsCompositeResponse() {
        RentalCheckoutRequestDto request = new RentalCheckoutRequestDto(buildRentalRequest(), buildPaymentRequest());

        Rental rentalEntity = new Rental();
        rentalEntity.setInventoryId(INVENTORY_ID);
        rentalEntity.setCustomerId(CUSTOMER_ID);
        rentalEntity.setStaffId(STAFF_ID);
        rentalEntity.setRentalDate(NOW);
        rentalEntity.setLastUpdate(NOW);

        Rental savedRental = new Rental();
        savedRental.setId(10);
        savedRental.setInventoryId(INVENTORY_ID);
        savedRental.setCustomerId(CUSTOMER_ID);
        savedRental.setStaffId(STAFF_ID);
        savedRental.setRentalDate(NOW);
        savedRental.setLastUpdate(NOW);

        Payment paymentEntity = new Payment();
        paymentEntity.setCustomerId(CUSTOMER_ID);
        paymentEntity.setStaffId(STAFF_ID);
        paymentEntity.setAmount(AMOUNT);
        paymentEntity.setPaymentDate(NOW);
        paymentEntity.setLastUpdate(NOW);

        Payment savedPayment = new Payment();
        savedPayment.setId(20);
        savedPayment.setCustomerId(CUSTOMER_ID);
        savedPayment.setStaffId(STAFF_ID);
        savedPayment.setAmount(AMOUNT);
        savedPayment.setPaymentDate(NOW);
        savedPayment.setLastUpdate(NOW);
        savedPayment.setRental(savedRental);

        RentalResponseDto rentalResponse = new RentalResponseDto(10, NOW, INVENTORY_ID, CUSTOMER_ID, null, STAFF_ID, NOW);
        PaymentResponseDto paymentResponse = new PaymentResponseDto(20, CUSTOMER_ID, STAFF_ID, 10, AMOUNT, NOW, NOW);

        when(rentalMapper.toEntity(request.rental())).thenReturn(rentalEntity);
        when(rentalRepository.save(any(Rental.class))).thenReturn(savedRental);
        when(paymentMapper.toEntity(request.payment())).thenReturn(paymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(rentalRepository.findById(10)).thenReturn(Optional.of(savedRental));
        when(paymentRepository.findById(20)).thenReturn(Optional.of(savedPayment));
        when(rentalMapper.toResponseDto(savedRental)).thenReturn(rentalResponse);
        when(paymentMapper.toResponseDto(savedPayment)).thenReturn(paymentResponse);

        RentalCheckoutResponseDto result = rentalService.checkout(request);

        assertThat(result).isNotNull();
        assertThat(result.rental().id()).isEqualTo(10);
        assertThat(result.payment().id()).isEqualTo(20);
        assertThat(result.payment().rentalId()).isEqualTo(10);
        verify(rentalRepository).save(any(Rental.class));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("should reject checkout when payment customerId does not match rental customerId")
    void checkout_withMismatchedCustomer_throwsBadRequestException() {
        PaymentRequestDto badPayment = new PaymentRequestDto(null, 999, STAFF_ID, null, AMOUNT, NOW, NOW);
        RentalCheckoutRequestDto request = new RentalCheckoutRequestDto(buildRentalRequest(), badPayment);

        assertThatThrownBy(() -> rentalService.checkout(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Payment customerId must match rental customerId");

        verify(rentalRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
    }
}

