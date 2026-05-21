package com.me.learning.rental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.rental.dto.PaymentRequestDto;
import com.me.learning.rental.dto.PaymentResponseDto;
import com.me.learning.rental.dto.RentalRequestDto;
import com.me.learning.rental.entity.Payment;
import com.me.learning.rental.entity.Rental;
import com.me.learning.rental.mapper.PaymentMapper;
import com.me.learning.rental.repository.PaymentRepository;
import com.me.learning.rental.repository.RentalRepository;

/**
 * Unit tests for {@link PaymentServiceImpl}.
 *
 * <p>All collaborators ({@link PaymentRepository}, {@link RentalRepository},
 * {@link PaymentMapper}) are mocked with Mockito so tests run without a
 * Spring context, database or network.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentServiceImpl")
@SuppressWarnings("PMD.MethodNamingConventions")
class PaymentServiceImplTest {

    private static final Instant NOW = Instant.parse("2024-06-01T12:00:00Z");
    private static final int RENTAL_ID = 1;
    private static final int CUSTOMER_ID = 7;
    private static final short STAFF_ID = 2;
    private static final BigDecimal AMOUNT = new BigDecimal("4.99");

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Rental buildRental () {
        Rental rental = new Rental();
        rental.setId(RENTAL_ID);
        return rental;
    }

    private Payment buildPayment (Integer id) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setCustomerId(CUSTOMER_ID);
        payment.setStaffId(STAFF_ID);
        payment.setAmount(AMOUNT);
        payment.setPaymentDate(NOW);
        payment.setLastUpdate(NOW);
        payment.setRental(buildRental());
        return payment;
    }

    private PaymentRequestDto buildRequest () {
        return new PaymentRequestDto(
                null,
                CUSTOMER_ID,
                STAFF_ID,
                new RentalRequestDto(RENTAL_ID, NOW, 10, CUSTOMER_ID, null, STAFF_ID, NOW),
                AMOUNT,
                NOW,
                NOW
        );
    }

    private PaymentResponseDto buildResponse (Integer id) {
        return new PaymentResponseDto(id, CUSTOMER_ID, STAFF_ID, RENTAL_ID, AMOUNT, NOW, NOW);
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("should persist payment and return populated response DTO")
        void create_withValidRequest_returnsPopulatedResponseDto() {
            PaymentRequestDto request = buildRequest();
            Payment entity = buildPayment(null);
            Payment saved = buildPayment(42);
            PaymentResponseDto expected = buildResponse(42);

            when(paymentMapper.toEntity(request)).thenReturn(entity);
            when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(buildRental()));
            when(paymentRepository.save(any(Payment.class))).thenReturn(saved);
            when(paymentRepository.findById(42)).thenReturn(Optional.of(saved));
            when(paymentMapper.toResponseDto(saved)).thenReturn(expected);

            PaymentResponseDto result = paymentService.create(request);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(42);
            assertThat(result.rentalId()).isEqualTo(RENTAL_ID);
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("should allow payments without a linked rental")
        void create_withoutRental_savesPayment() {
            PaymentRequestDto request = new PaymentRequestDto(null, CUSTOMER_ID, STAFF_ID, null, AMOUNT, NOW, NOW);
            Payment entity = buildPayment(null);
            entity.setRental(null);
            Payment saved = buildPayment(1);
            saved.setRental(null);
            PaymentResponseDto expected = new PaymentResponseDto(1, CUSTOMER_ID, STAFF_ID, null, AMOUNT, NOW, NOW);

            when(paymentMapper.toEntity(request)).thenReturn(entity);
            when(paymentRepository.save(any(Payment.class))).thenReturn(saved);
            when(paymentRepository.findById(1)).thenReturn(Optional.of(saved));
            when(paymentMapper.toResponseDto(saved)).thenReturn(expected);

            PaymentResponseDto result = paymentService.create(request);

            assertThat(result).isNotNull();
            assertThat(result.rentalId()).isNull();
            verify(rentalRepository, never()).findById(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when rental ID does not exist")
        void create_whenRentalNotFound_throwsResourceNotFoundException() {
            PaymentRequestDto request = buildRequest();
            Payment entity = buildPayment(null);

            when(paymentMapper.toEntity(request)).thenReturn(entity);
            when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.create(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(paymentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("should overwrite all fields and return updated response DTO")
        void update_withValidId_returnsUpdatedResponseDto() {
            PaymentRequestDto request = buildRequest();
            Payment existing = buildPayment(1);
            PaymentResponseDto expected = buildResponse(1);

            when(paymentRepository.findById(1)).thenReturn(Optional.of(existing));
            when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(buildRental()));
            when(paymentRepository.save(any(Payment.class))).thenReturn(existing);
            when(paymentMapper.toResponseDto(existing)).thenReturn(expected);

            PaymentResponseDto result = paymentService.update(1, request);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1);
            verify(paymentRepository).save(existing);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when payment ID does not exist")
        void update_whenPaymentNotFound_throwsResourceNotFoundException() {
            PaymentRequestDto request = buildRequest();
            when(paymentRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.update(999, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("partialUpdate()")
    class PartialUpdateTests {

        @Test
        @DisplayName("should delegate to mapper.partialUpdate and persist result")
        void partialUpdate_withValidId_appliesPatchAndSaves() {
            PaymentRequestDto request = buildRequest();
            Payment existing = buildPayment(1);
            PaymentResponseDto expected = buildResponse(1);

            when(paymentRepository.findById(1)).thenReturn(Optional.of(existing));
            when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(buildRental()));
            when(paymentRepository.save(any(Payment.class))).thenReturn(existing);
            when(paymentMapper.toResponseDto(existing)).thenReturn(expected);

            PaymentResponseDto result = paymentService.partialUpdate(1, request);

            assertThat(result).isNotNull();
            verify(paymentMapper).partialUpdate(request, existing);
            verify(paymentRepository).save(existing);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when payment ID does not exist")
        void partialUpdate_whenPaymentNotFound_throwsResourceNotFoundException() {
            PaymentRequestDto request = buildRequest();
            when(paymentRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.partialUpdate(999, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("should return response DTO when payment exists")
        void findById_whenPaymentExists_returnsResponseDto() {
            Payment payment = buildPayment(1);
            PaymentResponseDto expected = buildResponse(1);

            when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));
            when(paymentMapper.toResponseDto(payment)).thenReturn(expected);

            PaymentResponseDto result = paymentService.findById(1);

            assertThat(result).isNotNull().isEqualTo(expected);
            assertThat(result.rentalId()).isEqualTo(RENTAL_ID);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("should return mapped list of all payments")
        void findAll_withMultiplePayments_returnsMappedList() {
            Payment p1 = buildPayment(1);
            Payment p2 = buildPayment(2);
            PaymentResponseDto dto1 = buildResponse(1);
            PaymentResponseDto dto2 = buildResponse(2);

            when(paymentRepository.findAll()).thenReturn(List.of(p1, p2));
            when(paymentMapper.toResponseDto(p1)).thenReturn(dto1);
            when(paymentMapper.toResponseDto(p2)).thenReturn(dto2);

            List<PaymentResponseDto> result = paymentService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(PaymentResponseDto::id).containsExactly(1, 2);
        }

        @Test
        @DisplayName("should return page of mapped payments")
        void findAllPaged_returnsMappedPage() {
            PageRequest pageable = PageRequest.of(0, 10);
            Payment payment = buildPayment(1);
            PaymentResponseDto dto = buildResponse(1);
            Page<Payment> page = new PageImpl<>(List.of(payment), pageable, 1);

            when(paymentRepository.findAll(pageable)).thenReturn(page);
            when(paymentMapper.toResponseDto(payment)).thenReturn(dto);

            Page<PaymentResponseDto> result = paymentService.findAll(pageable);

            assertThat(result.getTotalElements()).isEqualTo(1L);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().id()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("should invoke deleteById when payment exists")
        void delete_whenPaymentExists_callsDeleteById() {
            when(paymentRepository.existsById(1)).thenReturn(true);

            paymentService.delete(1);

            verify(paymentRepository).deleteById(1);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException without calling deleteById when not found")
        void delete_whenPaymentNotFound_throwsResourceNotFoundException() {
            when(paymentRepository.existsById(999)).thenReturn(false);

            assertThatThrownBy(() -> paymentService.delete(999))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(paymentRepository, never()).deleteById(any());
        }
    }
}

