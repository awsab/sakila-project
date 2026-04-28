package com.me.learning.parent.customerservice.service;

import java.time.Instant;
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

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.customerservice.dto.AddressRequest;
import com.me.learning.parent.customerservice.dto.AddressResponse;
import com.me.learning.parent.customerservice.dto.AddressUpdateRequest;
import com.me.learning.parent.customerservice.entity.Address;
import com.me.learning.parent.customerservice.entity.City;
import com.me.learning.parent.customerservice.mapper.AddressMapper;
import com.me.learning.parent.customerservice.repository.AddressRepository;
import com.me.learning.parent.customerservice.repository.CityRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author   : Prabakaran Ramu
 * Date     : 23/04/2026
 * Usage    : Unit tests for AddressServiceImpl
 */
@ExtendWith (MockitoExtension.class)
@DisplayName ("AddressServiceImpl")
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address address;
    private AddressResponse addressResponse;
    private AddressRequest addressRequest;
    private AddressUpdateRequest addressUpdateRequest;

    @BeforeEach
    void setUp () {
        City city = new City ();
        city.setId (1);

        address = new Address ();
        address.setId (1);
        address.setAddress ("47 MySakila Drive");
        address.setDistrict ("Alberta");
        address.setCity (city);
        address.setPhone ("250 754-2548");

        addressResponse = new AddressResponse (
                1, "47 MySakila Drive", null,
                "Alberta", 1, "Lethbridge", "T1K 5M9", "250 754-2548", Instant.now ()
        );

        addressRequest = new AddressRequest (
                "47 MySakila Drive", null, "Alberta", 1, "T1K 5M9", "250 754-2548"
        );

        addressUpdateRequest = new AddressUpdateRequest (
                "47 MySakila Drive", null, "Alberta", 1, "T1K 5M9", "250 754-2548"
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("createAddress")
    class CreateAddress {

        @Test
        @DisplayName ("should create and return address when city exists")
        void createAddress_Success () {
            when (cityRepository.existsById (addressRequest.cityId ())).thenReturn (true);
            when (addressMapper.toEntity (addressRequest)).thenReturn (address);
            when (addressRepository.save (address)).thenReturn (address);
            when (addressMapper.toResponse (address)).thenReturn (addressResponse);

            AddressResponse result = addressService.createAddress (addressRequest);

            assertThat (result).isNotNull ();
            assertThat (result.address ()).isEqualTo ("47 MySakila Drive");
            verify (addressRepository).save (address);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when city does not exist")
        void createAddress_CityNotFound () {
            when (cityRepository.existsById (addressRequest.cityId ())).thenReturn (false);

            assertThatThrownBy (() -> addressService.createAddress (addressRequest))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (addressRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("updateAddress")
    class UpdateAddress {

        @Test
        @DisplayName ("should update and return address when ID and city both exist")
        void updateAddress_Success () {
            when (addressRepository.findById (1)).thenReturn (Optional.of (address));
            when (cityRepository.existsById (addressUpdateRequest.cityId ())).thenReturn (true);
            when (addressRepository.save (address)).thenReturn (address);
            when (addressMapper.toResponse (address)).thenReturn (addressResponse);

            AddressResponse result = addressService.updateAddress (1, addressUpdateRequest);

            assertThat (result).isNotNull ();
            verify (addressRepository).save (address);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when address ID does not exist")
        void updateAddress_NotFound () {
            when (addressRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> addressService.updateAddress (99, addressUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when city ID does not exist during update")
        void updateAddress_CityNotFound () {
            when (addressRepository.findById (1)).thenReturn (Optional.of (address));
            when (cityRepository.existsById (addressUpdateRequest.cityId ())).thenReturn (false);

            assertThatThrownBy (() -> addressService.updateAddress (1, addressUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (addressRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("patchAddress")
    class PatchAddress {

        @Test
        @DisplayName ("should patch and return address when ID exists")
        void patchAddress_Success () {
            when (addressRepository.findById (1)).thenReturn (Optional.of (address));
            when (addressRepository.save (address)).thenReturn (address);
            when (addressMapper.toResponse (address)).thenReturn (addressResponse);

            AddressResponse result = addressService.patchAddress (1, addressUpdateRequest);

            assertThat (result).isNotNull ();
            verify (addressMapper).updateEntity (addressUpdateRequest, address);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when address ID does not exist")
        void patchAddress_NotFound () {
            when (addressRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> addressService.patchAddress (99, addressUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getAddressById")
    class GetAddressById {

        @Test
        @DisplayName ("should return address when ID exists")
        void getAddressById_Success () {
            when (addressRepository.findById (1)).thenReturn (Optional.of (address));
            when (addressMapper.toResponse (address)).thenReturn (addressResponse);

            AddressResponse result = addressService.getAddressById (1);

            assertThat (result).isNotNull ();
            assertThat (result.id ()).isEqualTo (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void getAddressById_NotFound () {
            when (addressRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> addressService.getAddressById (99))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getAllAddresses")
    class GetAllAddresses {

        @Test
        @DisplayName ("should return all addresses as a list")
        void getAllAddresses_List () {
            when (addressRepository.findAll ()).thenReturn (List.of (address));
            when (addressMapper.toResponseList (List.of (address))).thenReturn (List.of (addressResponse));

            List<AddressResponse> result = addressService.getAllAddresses ();

            assertThat (result).hasSize (1);
        }

        @Test
        @DisplayName ("should return paginated addresses")
        void getAllAddresses_Paginated () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Address> page = new PageImpl<> (List.of (address));
            when (addressRepository.findAll (pageable)).thenReturn (page);
            when (addressMapper.toResponse (address)).thenReturn (addressResponse);

            Page<AddressResponse> result = addressService.getAllAddresses (pageable);

            assertThat (result.getContent ()).hasSize (1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getAddressesByCityId")
    class GetAddressesByCityId {

        @Test
        @DisplayName ("should return addresses for the given city ID")
        void getAddressesByCityId_Success () {
            when (addressRepository.findByCityId (1)).thenReturn (List.of (address));
            when (addressMapper.toResponseList (List.of (address))).thenReturn (List.of (addressResponse));

            List<AddressResponse> result = addressService.getAddressesByCityId (1);

            assertThat (result).hasSize (1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("deleteAddress")
    class DeleteAddress {

        @Test
        @DisplayName ("should delete address when ID exists")
        void deleteAddress_Success () {
            when (addressRepository.existsById (1)).thenReturn (true);

            addressService.deleteAddress (1);

            verify (addressRepository).deleteById (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void deleteAddress_NotFound () {
            when (addressRepository.existsById (99)).thenReturn (false);

            assertThatThrownBy (() -> addressService.deleteAddress (99))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (addressRepository, never ()).deleteById (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("existsById")
    class ExistsById {

        @Test
        @DisplayName ("should return true when address exists")
        void existsById_True () {
            when (addressRepository.existsById (1)).thenReturn (true);
            assertThat (addressService.existsById (1)).isTrue ();
        }

        @Test
        @DisplayName ("should return false when address does not exist")
        void existsById_False () {
            when (addressRepository.existsById (99)).thenReturn (false);
            assertThat (addressService.existsById (99)).isFalse ();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("countAddresses")
    class CountAddresses {

        @Test
        @DisplayName ("should return total address count")
        void countAddresses_Success () {
            when (addressRepository.count ()).thenReturn (603L);
            assertThat (addressService.countAddresses ()).isEqualTo (603L);
        }
    }
}

