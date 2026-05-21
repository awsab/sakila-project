package com.me.learning.inventory.service;

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
import com.me.learning.inventory.dto.InventoryRequestDto;
import com.me.learning.inventory.dto.InventoryResponseDto;
import com.me.learning.inventory.dto.StoreRefDto;
import com.me.learning.inventory.entity.Inventory;
import com.me.learning.inventory.entity.Store;
import com.me.learning.inventory.mapper.InventoryMapper;
import com.me.learning.inventory.repository.InventoryRepository;
import com.me.learning.inventory.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryServiceImpl")
@SuppressWarnings("PMD.MethodNamingConventions")
class InventoryServiceImplTest {

    private static final Instant NOW = Instant.parse("2025-01-01T00:00:00Z");
    private static final short STORE_ID = 1;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Store buildStore() {
        Store store = new Store();
        store.setId(STORE_ID);
        store.setManagerStaffId((short) 1);
        store.setAddressId(10);
        store.setLastUpdate(NOW);
        return store;
    }

    private Inventory buildInventory(Integer id) {
        Inventory inventory = new Inventory();
        inventory.setId(id);
        inventory.setFilmId(100);
        inventory.setStore(buildStore());
        inventory.setLastUpdate(NOW);
        return inventory;
    }

    private InventoryRequestDto buildRequest() {
        return new InventoryRequestDto(null, 100, new StoreRefDto(STORE_ID), NOW);
    }

    private InventoryResponseDto buildResponse(Integer id) {
        return new InventoryResponseDto(id, 100, STORE_ID, NOW);
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("should persist inventory and return response DTO")
        void create_withValidRequest_returnsResponseDto() {
            InventoryRequestDto request = buildRequest();
            Inventory entity = buildInventory(null);
            Inventory saved = buildInventory(42);
            InventoryResponseDto expected = buildResponse(42);

            when(inventoryMapper.toEntity(request)).thenReturn(entity);
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(buildStore()));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);
            when(inventoryRepository.findById(42)).thenReturn(Optional.of(saved));
            when(inventoryMapper.toResponseDto(saved)).thenReturn(expected);

            InventoryResponseDto result = inventoryService.create(request);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(42);
            assertThat(result.filmId()).isEqualTo(100);
            assertThat(result.storeId()).isEqualTo(STORE_ID);
            verify(inventoryRepository).save(any(Inventory.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when store does not exist")
        void create_whenStoreNotFound_throwsResourceNotFoundException() {
            InventoryRequestDto request = buildRequest();
            Inventory entity = buildInventory(null);

            when(inventoryMapper.toEntity(request)).thenReturn(entity);
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.create(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(inventoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("should overwrite fields and return updated response DTO")
        void update_withValidId_returnsUpdatedResponseDto() {
            InventoryRequestDto request = buildRequest();
            Inventory existing = buildInventory(1);
            InventoryResponseDto expected = buildResponse(1);

            when(inventoryRepository.findById(1)).thenReturn(Optional.of(existing));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(existing);
            when(inventoryMapper.toResponseDto(existing)).thenReturn(expected);

            InventoryResponseDto result = inventoryService.update(1, request);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1);
            verify(inventoryRepository).save(existing);
        }

        @Test
        @DisplayName("should load new store when store ID changes")
        void update_whenStoreIdChanges_loadsNewStore() {
            short newStoreId = 2;
            InventoryRequestDto request = new InventoryRequestDto(null, 101, new StoreRefDto(newStoreId), NOW);
            Inventory existing = buildInventory(1);
            Store newStore = buildStore();
            newStore.setId(newStoreId);

            when(inventoryRepository.findById(1)).thenReturn(Optional.of(existing));
            when(storeRepository.findById(newStoreId)).thenReturn(Optional.of(newStore));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(existing);
            when(inventoryMapper.toResponseDto(existing)).thenReturn(buildResponse(1));

            inventoryService.update(1, request);

            verify(storeRepository).findById(newStoreId);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when inventory does not exist")
        void update_whenInventoryNotFound_throwsResourceNotFoundException() {
            when(inventoryRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.update(999, buildRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("partialUpdate()")
    class PartialUpdateTests {

        @Test
        @DisplayName("should delegate to mapper.partialUpdate and persist result")
        void partialUpdate_withValidId_appliesPatchAndSaves() {
            InventoryRequestDto request = buildRequest();
            Inventory existing = buildInventory(1);
            InventoryResponseDto expected = buildResponse(1);

            when(inventoryRepository.findById(1)).thenReturn(Optional.of(existing));
            when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(buildStore()));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(existing);
            when(inventoryMapper.toResponseDto(existing)).thenReturn(expected);

            InventoryResponseDto result = inventoryService.partialUpdate(1, request);

            assertThat(result).isNotNull();
            verify(inventoryMapper).partialUpdate(request, existing);
            verify(inventoryRepository).save(existing);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when inventory does not exist")
        void partialUpdate_whenInventoryNotFound_throwsResourceNotFoundException() {
            when(inventoryRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.partialUpdate(999, buildRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("should return response DTO when inventory exists")
        void findById_whenInventoryExists_returnsResponseDto() {
            Inventory inventory = buildInventory(1);
            InventoryResponseDto expected = buildResponse(1);

            when(inventoryRepository.findById(1)).thenReturn(Optional.of(inventory));
            when(inventoryMapper.toResponseDto(inventory)).thenReturn(expected);

            InventoryResponseDto result = inventoryService.findById(1);

            assertThat(result).isNotNull().isEqualTo(expected);
            assertThat(result.id()).isEqualTo(1);
            assertThat(result.filmId()).isEqualTo(100);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when inventory does not exist")
        void findById_whenInventoryNotFound_throwsResourceNotFoundException() {
            when(inventoryRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.findById(999))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findAll() - unpaged")
    class FindAllUnpagedTests {

        @Test
        @DisplayName("should return mapped list of all inventories")
        void findAll_withMultipleInventories_returnsMappedList() {
            Inventory i1 = buildInventory(1);
            Inventory i2 = buildInventory(2);
            InventoryResponseDto dto1 = buildResponse(1);
            InventoryResponseDto dto2 = buildResponse(2);

            when(inventoryRepository.findAll()).thenReturn(List.of(i1, i2));
            when(inventoryMapper.toResponseDto(i1)).thenReturn(dto1);
            when(inventoryMapper.toResponseDto(i2)).thenReturn(dto2);

            List<InventoryResponseDto> result = inventoryService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(InventoryResponseDto::id).containsExactly(1, 2);
        }

        @Test
        @DisplayName("should return empty list when repository has no inventory")
        void findAll_whenRepositoryEmpty_returnsEmptyList() {
            when(inventoryRepository.findAll()).thenReturn(List.of());

            assertThat(inventoryService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll(Pageable)")
    class FindAllPagedTests {

        @Test
        @DisplayName("should return page of mapped inventories")
        void findAllPaged_returnsPageWithMappedContent() {
            PageRequest pageable = PageRequest.of(0, 10);
            Inventory inventory = buildInventory(1);
            InventoryResponseDto expected = buildResponse(1);
            Page<Inventory> page = new PageImpl<>(List.of(inventory), pageable, 1);

            when(inventoryRepository.findAll(pageable)).thenReturn(page);
            when(inventoryMapper.toResponseDto(inventory)).thenReturn(expected);

            Page<InventoryResponseDto> result = inventoryService.findAll(pageable);

            assertThat(result.getTotalElements()).isEqualTo(1L);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).id()).isEqualTo(1);
        }

        @Test
        @DisplayName("should return empty page when repository has no inventory")
        void findAllPaged_whenEmpty_returnsEmptyPage() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Inventory> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(inventoryRepository.findAll(pageable)).thenReturn(emptyPage);

            Page<InventoryResponseDto> result = inventoryService.findAll(pageable);

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("should invoke deleteById when inventory exists")
        void delete_whenInventoryExists_callsDeleteById() {
            when(inventoryRepository.existsById(1)).thenReturn(true);

            inventoryService.delete(1);

            verify(inventoryRepository).deleteById(1);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when inventory does not exist")
        void delete_whenInventoryNotFound_throwsResourceNotFoundException() {
            when(inventoryRepository.existsById(999)).thenReturn(false);

            assertThatThrownBy(() -> inventoryService.delete(999))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(inventoryRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("existsById()")
    class ExistsByIdTests {

        @Test
        @DisplayName("should return true when inventory exists")
        void existsById_whenInventoryExists_returnsTrue() {
            when(inventoryRepository.existsById(1)).thenReturn(true);

            assertThat(inventoryService.existsById(1)).isTrue();
        }

        @Test
        @DisplayName("should return false when inventory does not exist")
        void existsById_whenInventoryAbsent_returnsFalse() {
            when(inventoryRepository.existsById(999)).thenReturn(false);

            assertThat(inventoryService.existsById(999)).isFalse();
        }
    }

    @Nested
    @DisplayName("count()")
    class CountTests {

        @Test
        @DisplayName("should return total number of inventory records")
        void count_delegatesToRepository() {
            when(inventoryRepository.count()).thenReturn(7L);

            assertThat(inventoryService.count()).isEqualTo(7L);
        }

        @Test
        @DisplayName("should return zero when repository is empty")
        void count_whenEmpty_returnsZero() {
            when(inventoryRepository.count()).thenReturn(0L);

            assertThat(inventoryService.count()).isZero();
        }
    }
}

