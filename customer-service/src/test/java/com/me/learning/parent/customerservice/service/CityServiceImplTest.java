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
import com.me.learning.parent.customerservice.dto.CityRequest;
import com.me.learning.parent.customerservice.dto.CityResponse;
import com.me.learning.parent.customerservice.dto.CityUpdateRequest;
import com.me.learning.parent.customerservice.entity.City;
import com.me.learning.parent.customerservice.entity.Country;
import com.me.learning.parent.customerservice.mapper.CityMapper;
import com.me.learning.parent.customerservice.repository.CityRepository;
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
 * Usage    : Unit tests for CityServiceImpl
 */
@ExtendWith (MockitoExtension.class)
@DisplayName ("CityServiceImpl")
class CityServiceImplTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CityServiceImpl cityService;

    private City city;
    private CityResponse cityResponse;
    private CityRequest cityRequest;
    private CityUpdateRequest cityUpdateRequest;

    @BeforeEach
    void setUp () {
        Country country = new Country ();
        country.setId (1);
        country.setCountry ("Afghanistan");

        city = new City ();
        city.setId (1);
        city.setCity ("Kabul");
        city.setCountry (country);
        city.setLastUpdate (Instant.now ());

        cityResponse = new CityResponse (1, "Kabul", 1, "Afghanistan", Instant.now ());

        cityRequest = new CityRequest ("Kabul", 1);

        cityUpdateRequest = new CityUpdateRequest ("Kabul", 1);
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("createCity")
    class CreateCity {

        @Test
        @DisplayName ("should create and return city when country exists and city is unique")
        void createCity_Success () {
            when (countryRepository.existsById (cityRequest.countryId ())).thenReturn (true);
            when (cityRepository.existsByCityIgnoreCaseAndCountryId (cityRequest.city (), cityRequest.countryId ()))
                    .thenReturn (false);
            when (cityMapper.toEntity (cityRequest)).thenReturn (city);
            when (cityRepository.save (city)).thenReturn (city);
            when (cityMapper.toResponse (city)).thenReturn (cityResponse);

            CityResponse result = cityService.createCity (cityRequest);

            assertThat (result).isNotNull ();
            assertThat (result.city ()).isEqualTo ("Kabul");
            verify (cityRepository).save (city);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when country does not exist")
        void createCity_CountryNotFound () {
            when (countryRepository.existsById (cityRequest.countryId ())).thenReturn (false);

            assertThatThrownBy (() -> cityService.createCity (cityRequest))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (cityRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when city already exists in that country")
        void createCity_Duplicate () {
            when (countryRepository.existsById (cityRequest.countryId ())).thenReturn (true);
            when (cityRepository.existsByCityIgnoreCaseAndCountryId (cityRequest.city (), cityRequest.countryId ()))
                    .thenReturn (true);

            assertThatThrownBy (() -> cityService.createCity (cityRequest))
                    .isInstanceOf (DuplicateResourceException.class);

            verify (cityRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("updateCity")
    class UpdateCity {

        @Test
        @DisplayName ("should update and return city when ID and country both exist")
        void updateCity_Success () {
            when (cityRepository.findById (1)).thenReturn (Optional.of (city));
            when (countryRepository.existsById (cityUpdateRequest.countryId ())).thenReturn (true);
            when (cityRepository.save (city)).thenReturn (city);
            when (cityMapper.toResponse (city)).thenReturn (cityResponse);

            CityResponse result = cityService.updateCity (1, cityUpdateRequest);

            assertThat (result).isNotNull ();
            verify (cityRepository).save (city);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when city ID does not exist")
        void updateCity_NotFound () {
            when (cityRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> cityService.updateCity (99, cityUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when country ID does not exist during update")
        void updateCity_CountryNotFound () {
            when (cityRepository.findById (1)).thenReturn (Optional.of (city));
            when (countryRepository.existsById (cityUpdateRequest.countryId ())).thenReturn (false);

            assertThatThrownBy (() -> cityService.updateCity (1, cityUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (cityRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("patchCity")
    class PatchCity {

        @Test
        @DisplayName ("should patch and return city when ID exists")
        void patchCity_Success () {
            when (cityRepository.findById (1)).thenReturn (Optional.of (city));
            when (cityRepository.save (city)).thenReturn (city);
            when (cityMapper.toResponse (city)).thenReturn (cityResponse);

            CityResponse result = cityService.patchCity (1, cityUpdateRequest);

            assertThat (result).isNotNull ();
            verify (cityMapper).updateEntity (cityUpdateRequest, city);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when city ID does not exist")
        void patchCity_NotFound () {
            when (cityRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> cityService.patchCity (99, cityUpdateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getCityById")
    class GetCityById {

        @Test
        @DisplayName ("should return city when ID exists")
        void getCityById_Success () {
            when (cityRepository.findById (1)).thenReturn (Optional.of (city));
            when (cityMapper.toResponse (city)).thenReturn (cityResponse);

            CityResponse result = cityService.getCityById (1);

            assertThat (result).isNotNull ();
            assertThat (result.id ()).isEqualTo (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void getCityById_NotFound () {
            when (cityRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> cityService.getCityById (99))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getAllCities")
    class GetAllCities {

        @Test
        @DisplayName ("should return all cities as a list")
        void getAllCities_List () {
            when (cityRepository.findAll ()).thenReturn (List.of (city));
            when (cityMapper.toResponseList (List.of (city))).thenReturn (List.of (cityResponse));

            List<CityResponse> result = cityService.getAllCities ();

            assertThat (result).hasSize (1);
        }

        @Test
        @DisplayName ("should return paginated cities")
        void getAllCities_Paginated () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<City> page = new PageImpl<> (List.of (city));
            when (cityRepository.findAll (pageable)).thenReturn (page);
            when (cityMapper.toResponse (city)).thenReturn (cityResponse);

            Page<CityResponse> result = cityService.getAllCities (pageable);

            assertThat (result.getContent ()).hasSize (1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("getCitiesByCountryId")
    class GetCitiesByCountryId {

        @Test
        @DisplayName ("should return cities belonging to the given country ID")
        void getCitiesByCountryId_Success () {
            when (cityRepository.findByCountryId (1)).thenReturn (List.of (city));
            when (cityMapper.toResponseList (List.of (city))).thenReturn (List.of (cityResponse));

            List<CityResponse> result = cityService.getCitiesByCountryId (1);

            assertThat (result).hasSize (1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("deleteCity")
    class DeleteCity {

        @Test
        @DisplayName ("should delete city when ID exists")
        void deleteCity_Success () {
            when (cityRepository.existsById (1)).thenReturn (true);

            cityService.deleteCity (1);

            verify (cityRepository).deleteById (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void deleteCity_NotFound () {
            when (cityRepository.existsById (99)).thenReturn (false);

            assertThatThrownBy (() -> cityService.deleteCity (99))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (cityRepository, never ()).deleteById (any ());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("existsById")
    class ExistsById {

        @Test
        @DisplayName ("should return true when city exists")
        void existsById_True () {
            when (cityRepository.existsById (1)).thenReturn (true);
            assertThat (cityService.existsById (1)).isTrue ();
        }

        @Test
        @DisplayName ("should return false when city does not exist")
        void existsById_False () {
            when (cityRepository.existsById (99)).thenReturn (false);
            assertThat (cityService.existsById (99)).isFalse ();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName ("countCities")
    class CountCities {

        @Test
        @DisplayName ("should return total city count")
        void countCities_Success () {
            when (cityRepository.count ()).thenReturn (600L);
            assertThat (cityService.countCities ()).isEqualTo (600L);
        }
    }
}

