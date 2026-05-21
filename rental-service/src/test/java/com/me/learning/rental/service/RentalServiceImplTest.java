package com.me.learning.rental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.me.learning.rental.dto.RentalRequestDto;
import com.me.learning.rental.dto.RentalResponseDto;
import com.me.learning.rental.entity.Rental;
import com.me.learning.rental.mapper.RentalMapper;
import com.me.learning.rental.repository.RentalRepository;

/**
 * Unit tests for {@link RentalServiceImpl}.
 *
 * <p>All collaborators ({@link RentalRepository}, {@link RentalMapper}) are mocked
 * with Mockito so tests run without a Spring context, database or network.
 *
 * <p>Assertions use AssertJ for a fluent, readable style.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RentalServiceImpl")
@SuppressWarnings("PMD.MethodNamingConventions")
class RentalServiceImplTest {

    /* ── Constants ─────────────────────────────────────────────────────── */

    private static final Instant NOW          = Instant.parse("2024-06-01T12:00:00Z");
    private static final int     INVENTORY_ID = 1;
    private static final int     CUSTOMER_ID  = 1;
    private static final short   STAFF_ID     = 1;

    /* ── Mocks ──────────────────────────────────────────────────────────── */

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalMapper rentalMapper;

    @InjectMocks
    private RentalServiceImpl rentalService;

    /* ── Test-data builders ─────────────────────────────────────────────── */

    private Rental buildRental (Integer id) {
        Rental rental = new Rental();
        rental.setId(id);
        rental.setRentalDate(NOW);
        rental.setInventoryId(INVENTORY_ID);
        rental.setCustomerId(CUSTOMER_ID);
        rental.setStaffId(STAFF_ID);
        rental.setLastUpdate(NOW);
        return rental;
    }

    private RentalRequestDto buildRequest () {
        return new RentalRequestDto(null, NOW, INVENTORY_ID, CUSTOMER_ID, null, STAFF_ID, NOW);
    }

    private RentalResponseDto buildResponse (Integer id) {
        return new RentalResponseDto(id, NOW, INVENTORY_ID, CUSTOMER_ID, null, STAFF_ID, NOW);
    }

    /* ══════════════════════════════════════════════════════════════════════
       create()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("should persist rental and return populated response DTO")
        void create_withValidRequest_returnsPopulatedResponseDto() {
            RentalRequestDto request    = buildRequest();
            Rental entity               = buildRental(null);
            Rental saved                = buildRental(42);
            RentalResponseDto expected  = buildResponse(42);

            when(rentalMapper.toEntity(request)).thenReturn(entity);
            when(rentalRepository.save(any(Rental.class))).thenReturn(saved);
            when(rentalRepository.findById(42)).thenReturn(Optional.of(saved));
            when(rentalMapper.toResponseDto(saved)).thenReturn(expected);

            RentalResponseDto result = rentalService.create(request);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(42);
            assertThat(result.inventoryId()).isEqualTo(INVENTORY_ID);
            assertThat(result.customerId()).isEqualTo(CUSTOMER_ID);
            assertThat(result.staffId()).isEqualTo(STAFF_ID);
            verify(rentalRepository).save(any(Rental.class));
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       update()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("should overwrite all fields and return updated response DTO")
        void update_withValidId_returnsUpdatedResponseDto() {
            RentalRequestDto request    = buildRequest();
            Rental existing             = buildRental(1);
            RentalResponseDto expected  = buildResponse(1);

            when(rentalRepository.findById(1)).thenReturn(Optional.of(existing));
            when(rentalRepository.save(any(Rental.class))).thenReturn(existing);
            when(rentalMapper.toResponseDto(existing)).thenReturn(expected);

            RentalResponseDto result = rentalService.update(1, request);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1);
            verify(rentalRepository).save(existing);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when rental ID does not exist")
        void update_whenRentalNotFound_throwsResourceNotFoundException() {
            when(rentalRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> rentalService.update(999, buildRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       partialUpdate()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName("partialUpdate()")
    class PartialUpdateTests {

        @Test
        @DisplayName("should delegate to mapper.partialUpdate and persist result")
        void partialUpdate_withValidId_appliesPatchAndSaves() {
            RentalRequestDto request    = buildRequest();
            Rental existing             = buildRental(1);
            RentalResponseDto expected  = buildResponse(1);

            when(rentalRepository.findById(1)).thenReturn(Optional.of(existing));
            when(rentalRepository.save(any(Rental.class))).thenReturn(existing);
            when(rentalMapper.toResponseDto(existing)).thenReturn(expected);

            RentalResponseDto result = rentalService.partialUpdate(1, request);

            assertThat(result).isNotNull();
            verify(rentalMapper).partialUpdate(request, existing);
            verify(rentalRepository).save(existing);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when rental ID does not exist")
        void partialUpdate_whenRentalNotFound_throwsResourceNotFoundException() {
            when(rentalRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> rentalService.partialUpdate(999, buildRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       findById()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("should return response DTO when rental exists")
        void findById_whenRentalExists_returnsResponseDto() {
            Rental rental               = buildRental(1);
            RentalResponseDto expected  = buildResponse(1);

            when(rentalRepository.findById(1)).thenReturn(Optional.of(rental));
            when(rentalMapper.toResponseDto(rental)).thenReturn(expected);

            RentalResponseDto result = rentalService.findById(1);

            assertThat(result).isNotNull().isEqualTo(expected);
            assertThat(result.id()).isEqualTo(1);
            assertThat(result.inventoryId()).isEqualTo(INVENTORY_ID);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when rental does not exist")
        void findById_whenRentalNotFound_throwsResourceNotFoundException() {
            when(rentalRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> rentalService.findById(999))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       findAll() — unpaged
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName("findAll() — unpaged")
    class FindAllUnpagedTests {

        @Test
        @DisplayName("should return mapped list of all rentals")
        void findAll_withMultipleRentals_returnsMappedList() {
            Rental r1               = buildRental(1);
            Rental r2               = buildRental(2);
            RentalResponseDto dto1  = buildResponse(1);
            RentalResponseDto dto2  = buildResponse(2);

            when(rentalRepository.findAll()).thenReturn(List.of(r1, r2));
            when(rentalMapper.toResponseDto(r1)).thenReturn(dto1);
            when(rentalMapper.toResponseDto(r2)).thenReturn(dto2);

            List<RentalResponseDto> result = rentalService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(RentalResponseDto::id).containsExactly(1, 2);
        }

        @Test
        @DisplayName("should return empty list when repository has no rentals")
        void findAll_whenRepositoryEmpty_returnsEmptyList() {
            when(rentalRepository.findAll()).thenReturn(List.of());

            assertThat(rentalService.findAll()).isEmpty();
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       findAll(Pageable)
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName("findAll(Pageable)")
    class FindAllPagedTests {

        @Test
        @DisplayName("should return page of mapped rentals")
        void findAllPaged_returnsPageWithMappedContent() {
            PageRequest pageable        = PageRequest.of(0, 10);
            Rental rental               = buildRental(1);
            RentalResponseDto expected  = buildResponse(1);
            Page<Rental> page           = new PageImpl<>(List.of(rental), pageable, 1);

            when(rentalRepository.findAll(pageable)).thenReturn(page);
            when(rentalMapper.toResponseDto(rental)).thenReturn(expected);

            Page<RentalResponseDto> result = rentalService.findAll(pageable);

            assertThat(result.getTotalElements()).isEqualTo(1L);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).id()).isEqualTo(1);
        }

        @Test
        @DisplayName("should return empty page when repository has no rentals")
        void findAllPaged_whenEmpty_returnsEmptyPage() {
            PageRequest pageable         = PageRequest.of(0, 10);
            Page<Rental> emptyPage       = new PageImpl<>(List.of(), pageable, 0);

            when(rentalRepository.findAll(pageable)).thenReturn(emptyPage);

            Page<RentalResponseDto> result = rentalService.findAll(pageable);

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       delete()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("should invoke deleteById when rental exists")
        void delete_whenRentalExists_callsDeleteById() {
            when(rentalRepository.existsById(1)).thenReturn(true);

            rentalService.delete(1);

            verify(rentalRepository).deleteById(1);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException without calling deleteById when not found")
        void delete_whenRentalNotFound_throwsResourceNotFoundException() {
            when(rentalRepository.existsById(999)).thenReturn(false);

            assertThatThrownBy(() -> rentalService.delete(999))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(rentalRepository, never()).deleteById(any());
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       existsById()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName("existsById()")
    class ExistsByIdTests {

        @Test
        @DisplayName("should return true when rental exists in repository")
        void existsById_whenRentalExists_returnsTrue() {
            when(rentalRepository.existsById(1)).thenReturn(true);

            assertThat(rentalService.existsById(1)).isTrue();
        }

        @Test
        @DisplayName("should return false when rental does not exist in repository")
        void existsById_whenRentalAbsent_returnsFalse() {
            when(rentalRepository.existsById(999)).thenReturn(false);

            assertThat(rentalService.existsById(999)).isFalse();
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       count()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName("count()")
    class CountTests {

        @Test
        @DisplayName("should return total number of rentals from repository")
        void count_delegatesToRepository() {
            when(rentalRepository.count()).thenReturn(7L);

            assertThat(rentalService.count()).isEqualTo(7L);
        }

        @Test
        @DisplayName("should return zero when repository is empty")
        void count_whenEmpty_returnsZero() {
            when(rentalRepository.count()).thenReturn(0L);

            assertThat(rentalService.count()).isZero();
        }
    }
}

