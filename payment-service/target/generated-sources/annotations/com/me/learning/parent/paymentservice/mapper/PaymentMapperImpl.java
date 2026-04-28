package com.me.learning.parent.paymentservice.mapper;

import com.me.learning.parent.paymentservice.dto.PaymentRequest;
import com.me.learning.parent.paymentservice.dto.PaymentResponse;
import com.me.learning.parent.paymentservice.dto.PaymentUpdateRequest;
import com.me.learning.parent.paymentservice.entity.Payment;
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
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public Payment toEntity(PaymentRequest request) {
        if ( request == null ) {
            return null;
        }

        Payment payment = new Payment();

        return payment;
    }

    @Override
    public PaymentResponse toResponse(Payment entity) {
        if ( entity == null ) {
            return null;
        }

        Integer id = null;
        Instant lastUpdate = null;

        id = entity.getId();
        lastUpdate = entity.getLastUpdate();

        String name = null;

        PaymentResponse paymentResponse = new PaymentResponse( id, name, lastUpdate );

        return paymentResponse;
    }

    @Override
    public List<PaymentResponse> toResponseList(List<Payment> entities) {
        if ( entities == null ) {
            return null;
        }

        List<PaymentResponse> list = new ArrayList<PaymentResponse>( entities.size() );
        for ( Payment payment : entities ) {
            list.add( toResponse( payment ) );
        }

        return list;
    }

    @Override
    public void updateEntity(PaymentUpdateRequest request, Payment entity) {
        if ( request == null ) {
            return;
        }
    }
}
