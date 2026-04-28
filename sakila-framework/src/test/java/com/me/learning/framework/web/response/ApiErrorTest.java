package com.me.learning.framework.web.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ApiError")
class ApiErrorTest {

    @Test
    @DisplayName("builder sets code and detail")
    void builderSetsCodeAndDetail() {
        ApiError error = ApiError.builder()
                .code("EVS_404_001")
                .detail("Resource not found")
                .build();

        assertThat(error.getCode()).isEqualTo("EVS_404_001");
        assertThat(error.getDetail()).isEqualTo("Resource not found");
        assertThat(error.getFieldErrors()).isNull();
    }

    @Test
    @DisplayName("builder includes fieldErrors when set")
    void builderIncludesFieldErrors() {
        ApiError.FieldError fe = ApiError.FieldError.builder()
                .field("firstName")
                .rejectedValue("   ")
                .message("must not be blank")
                .build();

        ApiError error = ApiError.builder()
                .code("EVS_400_001")
                .detail("Validation failed")
                .fieldErrors(List.of(fe))
                .build();

        assertThat(error.getFieldErrors()).hasSize(1);
        assertThat(error.getFieldErrors().get(0).getField()).isEqualTo("firstName");
        assertThat(error.getFieldErrors().get(0).getRejectedValue()).isEqualTo("   ");
        assertThat(error.getFieldErrors().get(0).getMessage()).isEqualTo("must not be blank");
    }

    @Test
    @DisplayName("FieldError builder accepts null rejectedValue")
    void fieldErrorAcceptsNullRejectedValue() {
        ApiError.FieldError fe = ApiError.FieldError.builder()
                .field("email")
                .rejectedValue(null)
                .message("must not be null")
                .build();

        assertThat(fe.getRejectedValue()).isNull();
        assertThat(fe.getField()).isEqualTo("email");
    }
}
