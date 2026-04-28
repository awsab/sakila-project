package com.me.learning.parent.customerservice.dto;

import java.time.Instant;
import java.util.List;

public record CustomerDetailResponse(

        Integer id,
        Short storeId,
        String firstName,
        String lastName,
        String email,
        Integer addressId,
        AddressResponse address,
        List<PaymentDetailResponse> payments,
        Boolean active,
        Instant createDate,
        Instant lastUpdate

) {
}

