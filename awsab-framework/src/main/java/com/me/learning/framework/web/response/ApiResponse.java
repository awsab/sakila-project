package com.me.learning.framework.web.response;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard JSON response envelope for all eVisa REST APIs.
 *
 * <p>Every API response — success or error — is wrapped in this
 * structure so that consumers always get a consistent shape.
 *
 * <p>Success example:
 * <pre>{@code
 * {
 *   "status": 200,
 *   "success": true,
 *   "message": "Visa application retrieved",
 *   "data": { ... },
 *   "timestamp": "2024-11-07T10:30:00Z",
 *   "traceId": "4bf92f3577b34da6a3ce929d0e0e4736"
 * }
 * }</pre>
 *
 * <p>Error example:
 * <pre>{@code
 * {
 *   "status": 404,
 *   "success": false,
 *   "message": "Visa application not found",
 *   "error": { "code": "EVS_404_001", "detail": "..." },
 *   "timestamp": "2024-11-07T10:30:00Z",
 *   "traceId": "4bf92f3577b34da6a3ce929d0e0e4736"
 * }
 * }</pre>
 *
 * @param <T> type of the {@code data} payload
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** HTTP status code mirrored in the body for client convenience. */
    private final int status;

    /** {@code true} for 2xx responses, {@code false} otherwise. */
    private final boolean success;

    /** Human-readable result message. */
    private final String message;

    /** Response payload. {@code null} on error responses. */
    private final T data;

    /** Structured error detail. {@code null} on success responses. */
    private final ApiError errorDetail;

    /** Server-side timestamp of response generation (UTC). */
    @Builder.Default
    private final Instant timestamp = Instant.now();

    /**
     * OTel trace ID injected from MDC ({@code X-B3-TraceId} or W3C
     * {@code traceparent}).  Allows log-trace correlation in Grafana.
     */
    private final String traceId;

    // ── Factory helpers ──────────────────────────────────────

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> noContent() {
        return ApiResponse.<T>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .success(true)
                .message("Operation completed successfully")
                .build();
    }

    public ApiError getError() {
        return errorDetail;
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, ApiError error) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .success(false)
                .message(message)
                .errorDetail(error)
                .build();
    }
}
