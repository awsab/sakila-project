package com.me.learning.framework.web.errors;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

import com.me.learning.framework.web.response.ApiError;
import com.me.learning.framework.web.response.ApiResponse;


/**
 * Enterprise-wide REST exception handler.
 *
 * <p>Catches all exceptions thrown from any {@code @RestController} in
 * the application and maps them to the standard {@link ApiResponse} envelope.
 *
 * <p>Logging strategy:
 * <ul>
 *   <li>4xx client errors → {@code WARN} (client problem, not ours)</li>
 *   <li>5xx server errors → {@code ERROR} with full stack trace</li>
 * </ul>
 *
 * <p>This class is registered as a Spring bean via
 * {@link com.me.learning.framework.SakilaFrameworkAutoConfiguration}.
 * Child services do NOT need to declare their own {@code @ControllerAdvice}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Domain exceptions ────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(),
                ApiError.builder()
                        .code("EVS_404_001")
                        .detail(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyExists(
            ResourceAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Conflict [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.CONFLICT, ex.getMessage(),
                ApiError.builder()
                        .code("EVS_409_001")
                        .detail(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(
            BusinessRuleViolationException ex, HttpServletRequest request) {
        log.warn("Business rule violation [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(),
                ApiError.builder()
                        .code(ex.getErrorCode())
                        .detail(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleExternalService(
            ExternalServiceException ex, HttpServletRequest request) {
        log.error("External service failure [{}] calling [{}]: {}",
                request.getRequestURI(), ex.getServiceName(), ex.getMessage(), ex);
        return buildError(HttpStatus.BAD_GATEWAY, "External service unavailable",
                ApiError.builder()
                        .code("EVS_502_001")
                        .detail("Downstream service error: " + ex.getServiceName())
                        .build());
    }

    // ── Validation exceptions ────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation failure [{}]: {} field errors",
                request.getRequestURI(), ex.getBindingResult().getErrorCount());

        List<ApiError.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> ApiError.FieldError.builder()
                        .field(fe.getField())
                        .rejectedValue(fe.getRejectedValue())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();

        return buildError(HttpStatus.BAD_REQUEST, "Request validation failed",
                ApiError.builder()
                        .code("EVS_400_001")
                        .detail("One or more fields failed validation")
                        .fieldErrors(fieldErrors)
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Missing request parameter [{}]: {}", request.getRequestURI(), ex.getParameterName());
        return buildError(HttpStatus.BAD_REQUEST,
                "Required parameter missing: " + ex.getParameterName(),
                ApiError.builder().code("EVS_400_002").detail(ex.getMessage()).build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Type mismatch [{}]: parameter '{}' value '{}'",
                request.getRequestURI(), ex.getName(), ex.getValue());
        return buildError(HttpStatus.BAD_REQUEST,
                "Invalid value for parameter: " + ex.getName(),
                ApiError.builder().code("EVS_400_003").detail(ex.getMessage()).build());
    }

    // ── Additional domain exceptions ─────────────────────────

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest(
            InvalidRequestException ex, HttpServletRequest request) {
        log.warn("Invalid request [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(),
                ApiError.builder().code("EVS_400_004").detail(ex.getMessage()).build());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(
            DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("Duplicate resource [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.CONFLICT, ex.getMessage(),
                ApiError.builder().code("EVS_409_002").detail(ex.getMessage()).build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.CONFLICT, ex.getMessage(),
                ApiError.builder().code("EVS_409_003").detail(ex.getMessage()).build());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceException(
            ServiceException ex, HttpServletRequest request) {
        log.error("Service error [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An internal service error occurred",
                ApiError.builder().code("EVS_500_002").detail(ex.getMessage()).build());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {
        log.warn("Bad request [{}] entity='{}' key='{}': {}",
                request.getRequestURI(), ex.getEntityName(), ex.getErrorKey(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(),
                ApiError.builder().code("EVS_400_005").detail(ex.getMessage()).build());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("Entity not found [{}] entity='{}' key='{}': {}",
                request.getRequestURI(), ex.getEntityName(), ex.getErrorKey(), ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(),
                ApiError.builder().code("EVS_404_002").detail(ex.getMessage()).build());
    }

    // ── Security exceptions ──────────────────────────────────

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failure [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.UNAUTHORIZED, "Authentication required",
                ApiError.builder().code("EVS_401_001").detail("Invalid or missing token").build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, "Access denied",
                ApiError.builder().code("EVS_403_001").detail("Insufficient permissions").build());
    }

    // ── Catch-all ────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                ApiError.builder()
                        .code("EVS_500_001")
                        .detail("Contact support with traceId from response")
                        .build());
    }

    // ── Helper ───────────────────────────────────────────────

    private ResponseEntity<ApiResponse<Void>> buildError(
            HttpStatus status, String message, ApiError error) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(status, message, error));
    }
}
