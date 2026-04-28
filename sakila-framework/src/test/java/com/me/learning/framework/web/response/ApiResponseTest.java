package com.me.learning.framework.web.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("ApiResponse")
class ApiResponseTest {

    @Test
    @DisplayName("ok() factory creates successful 200 response with data")
    void okCreatesSuccessfulResponse() {
        String data = "payload";

        ApiResponse<String> response = ApiResponse.ok(data, "Retrieved successfully");

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Retrieved successfully");
        assertThat(response.getData()).isEqualTo("payload");
        assertThat(response.getError()).isNull();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("created() factory creates 201 response")
    void createdCreates201Response() {
        String data = "new-entity";

        ApiResponse<String> response = ApiResponse.created(data, "Created successfully");

        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Created successfully");
        assertThat(response.getData()).isEqualTo("new-entity");
        assertThat(response.getError()).isNull();
    }

    @Test
    @DisplayName("noContent() factory creates 204 response with no data")
    void noContentCreates204Response() {
        ApiResponse<Void> response = ApiResponse.noContent();

        assertThat(response.getStatus()).isEqualTo(204);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNull();
        assertThat(response.getMessage()).isEqualTo("Operation completed successfully");
    }

    @Test
    @DisplayName("error() factory creates failed response with error detail")
    void errorCreatesErrorResponse() {
        ApiError error = ApiError.builder()
                .code("EVS_404_001")
                .detail("Actor not found")
                .build();

        ApiResponse<Void> response = ApiResponse.error(HttpStatus.NOT_FOUND, "Not found", error);

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Not found");
        assertThat(response.getData()).isNull();
        assertThat(response.getError()).isSameAs(error);
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("builder allows setting traceId")
    void builderAllowsSettingTraceId() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(200)
                .success(true)
                .message("OK")
                .data("value")
                .traceId("abc-123")
                .build();

        assertThat(response.getTraceId()).isEqualTo("abc-123");
    }

    @Test
    @DisplayName("timestamp defaults to non-null Instant when using builder")
    void builderDefaultTimestampIsNotNull() {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(200)
                .success(true)
                .message("OK")
                .build();

        assertThat(response.getTimestamp()).isNotNull();
    }
}
