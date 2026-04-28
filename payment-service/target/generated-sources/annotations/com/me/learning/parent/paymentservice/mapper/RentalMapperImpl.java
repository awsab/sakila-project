package com.me.learning.parent.paymentservice.mapper;

import com.me.learning.parent.paymentservice.dto.RentalRequest;
import com.me.learning.parent.paymentservice.dto.RentalResponse;
import com.me.learning.parent.paymentservice.dto.RentalUpdateRequest;
import com.me.learning.parent.paymentservice.entity.Rental;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-21T14:12:52+0400",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.4 (Eclipse Adoptium)"
)
@Component
public class RentalMapperImpl implements RentalMapper {

    @Override
    public Rental toEntity(RentalRequest request) {
        if ( request == null ) {
            return null;
        }

        Rental rental = new Rental();

        return rental;
    }

    @Override
    public RentalResponse toResponse(Rental entity) {
        if ( entity == null ) {
            return null;
        }

        Integer id = null;
        Instant lastUpdate = null;

        id = entity.getId();
        lastUpdate = entity.getLastUpdate();

        String name = null;

        RentalResponse rentalResponse = new RentalResponse( id, name, lastUpdate );

        return rentalResponse;
    }

    @Override
    public List<RentalResponse> toResponseList(List<Rental> entities) {
        if ( entities == null ) {
            return null;
        }

        List<RentalResponse> list = new ArrayList<RentalResponse>( entities.size() );
        for ( Rental rental : entities ) {
            list.add( toResponse( rental ) );
        }

        return list;
    }

    @Override
    public void updateEntity(RentalUpdateRequest request, Rental entity) {
        if ( request == null ) {
            return;
        }
    }
}
