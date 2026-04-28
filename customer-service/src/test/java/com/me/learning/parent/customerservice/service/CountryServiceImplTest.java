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

import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.customerservice.dto.CountryRequest;
import com.me.learning.parent.customerservice.dto.CountryResponse;
import com.me.learning.parent.customerservice.dto.CountryUpdateRequest;
import com.me.learning.parent.customerservice.entity.Country;
import com.me.learning.parent.customerservice.mapper.CountryMapper;
import com.me.learning.parent.customerservice.repository.CountryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author   : Prabakaran Ramu
 * Date     : 23/04/2026
 * Usage    : Unit tests for CountryServiceImpl
 */
@ExtendWith (MockitoExtension.class)
@DisplayName ("CountryServiceImpl")
class CountryServiceImplTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryServiceImpl countryService;

    private Country country;
    private CountryResponse countryResponse;
    private CountryRequest countryRequest;
    private CountryUpdateRequest countryUpdateRequest;

    @BeforeEach
    void setUp () {
        country = new Country ();
        country.setId (1);
        country.setCountry ("Afghanistan");
        country.setLastUpdate (Instant.now ());

        countryResponse = new CountryResponse (1, "Afghanistan", Instant.now ());

        countryRequest = new CountryRequest ("Afghanistan");

        countryUpdateRequest = new CountryUpdateRequest ("Afghanistan");
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("createCountry")
    class CreateCountry {

        @Test
        @DisplayName ("should create and return country when name is unique")
        void createCountry_Success () {
            when (countryRepository.existsByCountryIgnoreCase (countryRequest.country ())).thenReturn (false);
            when (countryMapper.toEntity (countryRequest)).thenReturn (country);
            when (countryRepository.save (country)).thenReturn (country);
            when (countryMapper.toResponse (country)).thenReturn (countryResponse);

            CountryResponse result = countryService.createCountry (countryRequest);

            assertThat (result).isNotNull ();
            assertThat (result.country ()).isEqualTo ("Afghanistan");
            verify (countryRepository).save (country);
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when country name already exists")
        void createCountry_Duplicate () {
            when (countryRepository.existsByCountryIgnoreCase (countryRequest.country ())).thenReturn (true);

            assertThatThrownBy (() -> countryService.createCountry (countryRequest))
                    .isInstanceOf (DuplicateResourceException.class);

            verify (countryRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("updateCountry")
    class UpdateCountry {

        @Test
        @DisplayName ("should update and return country when ID exists and name is unique")
        void updateCountry_Success () {
            CountryUpdateRequest updateToNewName = new CountryUpdateRequest ("Albania");
            country.setCountry ("Afghanistan");

            when (countryRepository.findById (1)).thenReturn (Optional.of (country));
            when (countryRepository.existsByCountryIgnoreCase ("Albania")).thenReturn (false);
            when (countryRepository.save (country)).thenReturn (country);
            when (countryMapper.toResponse (country)).thenReturn (new CountryResponse (1, "Albania", Instant.now ()));

            CountryResponse result = countryService.updateCountry (1, updateToNewName);

            assertThat (result).isNotNull ();
            verify (countryRepository).save (country);
        }

        @Test
        @DisplayName ("should update country when name is unchanged (same case)")
        void updateCountry_SameName () {
            when (countryRepository.findById (1)).thenReturn (Optional.of (country));
            // same name → equalsIgnoreCase is true → no duplicate check triggered
            when (countryRepository.save (country)).thenReturn (country);
            when (countryMapper.toResponse (country)).thenReturn (countryResponse);

            CountryResponse result = countryService.updateCountry (1, countryUpdateRequest);

            assertThat (result).isNotNull ();
            verify (countryRepository, never ()).existsByCountryIgnoreCase (any ());
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when country ID does not exist")
        void updateCountry_NotFound () {
            when (countryRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> countryService.updateCountry (99, countryUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when new name belongs to another country")
        void updateCountry_DuplicateName () {
            CountryUpdateRequest requestWithTakenName = new CountryUpdateRequest ("Algeria");
            country.setCountry ("Afghanistan");

            when (countryRepository.findById (1)).thenReturn (Optional.of (country));
            when (countryRepository.existsByCountryIgnoreCase ("Algeria")).thenReturn (true);

            assertThatThrownBy (() -> countryService.updateCountry (1, requestWithTakenName))
                    .isInstanceOf (DuplicateResourceException.class);

            verify (countryRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("patchCountry")
    class PatchCountry {

        @Test
        @DisplayName ("should patch and return country when ID exists")
        void patchCountry_Success () {
            when (countryRepository.findById (1)).thenReturn (Optional.of (country));
            when (countryRepository.save (country)).thenReturn (country);
            when (countryMapper.toResponse (country)).thenReturn (countryResponse);

            CountryResponse result = countryService.patchCountry (1, countryUpdateRequest);

            assertThat (result).isNotNull ();
            verify (countryMapper).updateEntity (countryUpdateRequest, country);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when country ID does not exist")
        void patchCountry_NotFound () {
            when (countryRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> countryService.patchCountry (99, countryUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getCountryById")
    class GetCountryById {

        @Test
        @DisplayName ("should return country when ID exists")
        void getCountryById_Success () {
            when (countryRepository.findById (1)).thenReturn (Optional.of (country));
            when (countryMapper.toResponse (country)).thenReturn (countryResponse);

            CountryResponse result = countryService.getCountryById (1);

            assertThat (result).isNotNull ();
            assertThat (result.id ()).isEqualTo (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void getCountryById_NotFound () {
            when (countryRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> countryService.getCountryById (99))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getAllCountries")
    class GetAllCountries {

        @Test
        @DisplayName ("should return all countries as a list")
        void getAllCountries_List () {
            when (countryRepository.findAll ()).thenReturn (List.of (country));
            when (countryMapper.toResponseList (List.of (country))).thenReturn (List.of (countryResponse));

            List<CountryResponse> result = countryService.getAllCountries ();

            assertThat (result).hasSize (1);
        }

        @Test
        @DisplayName ("should return paginated countries")
        void getAllCountries_Paginated () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Country> page = new PageImpl<> (List.of (country));
            when (countryRepository.findAll (pageable)).thenReturn (page);
            when (countryMapper.toResponse (country)).thenReturn (countryResponse);

            Page<CountryResponse> result = countryService.getAllCountries (pageable);

            assertThat (result.getContent ()).hasSize (1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("deleteCountry")
    class DeleteCountry {

        @Test
        @DisplayName ("should delete country when ID exists")
        void deleteCountry_Success () {
            when (countryRepository.existsById (1)).thenReturn (true);

            countryService.deleteCountry (1);

            verify (countryRepository).deleteById (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void deleteCountry_NotFound () {
            when (countryRepository.existsById (99)).thenReturn (false);

            assertThatThrownBy (() -> countryService.deleteCountry (99))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (countryRepository, never ()).deleteById (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("existsById")
    class ExistsById {

        @Test
        @DisplayName ("should return true when country exists")
        void existsById_True () {
            when (countryRepository.existsById (1)).thenReturn (true);
            assertThat (countryService.existsById (1)).isTrue ();
        }

        @Test
        @DisplayName ("should return false when country does not exist")
        void existsById_False () {
            when (countryRepository.existsById (99)).thenReturn (false);
            assertThat (countryService.existsById (99)).isFalse ();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("countCountries")
    class CountCountries {

        @Test
        @DisplayName ("should return total country count")
        void countCountries_Success () {
            when (countryRepository.count ()).thenReturn (109L);
            assertThat (countryService.countCountries ()).isEqualTo (109L);
        }
    }
}

