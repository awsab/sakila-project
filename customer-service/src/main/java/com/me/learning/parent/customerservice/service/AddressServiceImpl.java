package com.me.learning.parent.customerservice.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.customerservice.dto.AddressRequest;
import com.me.learning.parent.customerservice.dto.AddressResponse;
import com.me.learning.parent.customerservice.dto.AddressUpdateRequest;
import com.me.learning.parent.customerservice.entity.Address;
import com.me.learning.parent.customerservice.mapper.AddressMapper;
import com.me.learning.parent.customerservice.repository.AddressRepository;
import com.me.learning.parent.customerservice.repository.CityRepository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Service implementation for Address entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class AddressServiceImpl implements AddressService {

    private static final String ADDRESS_RESOURCE = "Address";
    private static final String CITY_RESOURCE = "City";
    private static final String FIELD_ID = "id";

    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    @CacheEvict (value = {"addresses", "address"}, allEntries = true)
    public AddressResponse createAddress (AddressRequest request) {
        log.debug ("Creating new address: {}", request.address ());

        if ( !cityRepository.existsById (request.cityId ()) ) {
            throw new ResourceNotFoundException (CITY_RESOURCE, FIELD_ID, request.cityId ());
        }

        Address address = addressMapper.toEntity (request);
        Address saved = addressRepository.save (address);

        log.info ("Created address with ID: {}", saved.getId ());
        return addressMapper.toResponse (saved);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"addresses", "address"}, allEntries = true)
    public AddressResponse updateAddress (Integer id, AddressUpdateRequest request) {
        log.debug ("Updating address with ID: {}", id);

        Address existing = addressRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (ADDRESS_RESOURCE, FIELD_ID, id));

        if ( !cityRepository.existsById (request.cityId ()) ) {
            throw new ResourceNotFoundException (CITY_RESOURCE, FIELD_ID, request.cityId ());
        }

        existing.setAddress (request.address ());
        existing.setAddress2 (request.address2 ());
        existing.setDistrict (request.district ());
        existing.getCity ().setId (request.cityId ());
        existing.setPostalCode (request.postalCode ());
        existing.setPhone (request.phone ());

        Address updated = addressRepository.save (existing);

        log.info ("Updated address with ID: {}", id);
        return addressMapper.toResponse (updated);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"addresses", "address"}, allEntries = true)
    public AddressResponse patchAddress (Integer id, AddressUpdateRequest request) {
        log.debug ("Patching address with ID: {}", id);

        Address existing = addressRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (ADDRESS_RESOURCE, FIELD_ID, id));

        addressMapper.updateEntity (request, existing);
        Address updated = addressRepository.save (existing);

        log.info ("Patched address with ID: {}", id);
        return addressMapper.toResponse (updated);
    }

    @Override
    public AddressResponse getAddressById (Integer id) {
        log.debug ("Fetching address with ID: {}", id);

        Address address = addressRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (ADDRESS_RESOURCE, FIELD_ID, id));

        return addressMapper.toResponse (address);
    }

    @Override
    public List<AddressResponse> getAllAddresses () {
        log.debug ("Fetching all addresses");
        return addressMapper.toResponseList (addressRepository.findAll ());
    }

    @Override
    public Page<AddressResponse> getAllAddresses (Pageable pageable) {
        log.debug ("Fetching addresses â€” page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return addressRepository.findAll (pageable).map (addressMapper::toResponse);
    }

    @Override
    public List<AddressResponse> getAddressesByCityId (Integer cityId) {
        log.debug ("Fetching addresses for city ID: {}", cityId);
        return addressMapper.toResponseList (addressRepository.findByCityId (cityId));
    }

    @Override
    @Transactional
    @CacheEvict (value = {"addresses", "address"}, allEntries = true)
    public void deleteAddress (Integer id) {
        log.debug ("Deleting address with ID: {}", id);

        if ( !addressRepository.existsById (id) ) {
            throw new ResourceNotFoundException (ADDRESS_RESOURCE, FIELD_ID, id);
        }

        addressRepository.deleteById (id);
        log.info ("Deleted address with ID: {}", id);
    }

    @Override
    public boolean existsById (Integer id) {
        return addressRepository.existsById (id);
    }

    @Override
    public long countAddresses () {
        return addressRepository.count ();
    }
}

