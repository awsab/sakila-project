package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ExternalServiceException")
class ExternalServiceExceptionTest {

    @Test
    @DisplayName("2-arg constructor sets serviceName and message")
    void twoArgConstructorSetsServiceNameAndMessage() {
        ExternalServiceException ex = new ExternalServiceException("passport-service", "Call failed");

        assertThat(ex.getMessage()).isEqualTo("Call failed");
        assertThat(ex.getServiceName()).isEqualTo("passport-service");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("3-arg constructor propagates cause")
    void threeArgConstructorPropagatesCause() {
        Throwable cause = new RuntimeException("timeout");
        ExternalServiceException ex =
                new ExternalServiceException("payment-service", "Payment gateway down", cause);

        assertThat(ex.getMessage()).isEqualTo("Payment gateway down");
        assertThat(ex.getServiceName()).isEqualTo("payment-service");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
