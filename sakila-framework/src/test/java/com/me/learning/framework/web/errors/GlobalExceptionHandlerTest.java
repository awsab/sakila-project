package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.me.learning.framework.web.response.ApiResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    // ── Domain exceptions ─────────────────────────────────────────────────

    @Test
    @DisplayName("handleNotFound returns 404 with EVS_404_001")
    void handleNotFoundReturns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Actor", "id", 1);

        ResponseEntity<ApiResponse<Void>> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_404_001");
    }

    @Test
    @DisplayName("handleAlreadyExists returns 409 with EVS_409_001")
    void handleAlreadyExistsReturns409() {
        ResourceAlreadyExistsException ex =
                new ResourceAlreadyExistsException("Actor", "name", "Tom Hanks");

        ResponseEntity<ApiResponse<Void>> response = handler.handleAlreadyExists(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_409_001");
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("handleBusinessRule returns 422 with exception errorCode")
    void handleBusinessRuleReturns422() {
        BusinessRuleViolationException ex =
                new BusinessRuleViolationException("EVS_BIZ_001", "Visa expired");

        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessRule(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_BIZ_001");
    }

    @Test
    @DisplayName("handleExternalService returns 502 with EVS_502_001")
    void handleExternalServiceReturns502() {
        ExternalServiceException ex = new ExternalServiceException("payment-svc", "Down");

        ResponseEntity<ApiResponse<Void>> response = handler.handleExternalService(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_502_001");
        assertThat(response.getBody().getMessage()).isEqualTo("External service unavailable");
    }

    // ── Additional domain exceptions ─────────────────────────────────────

    @Test
    @DisplayName("handleInvalidRequest returns 400 with EVS_400_004")
    void handleInvalidRequestReturns400() {
        InvalidRequestException ex = new InvalidRequestException("Malformed payload");

        ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_400_004");
    }

    @Test
    @DisplayName("handleDuplicateResource returns 409 with EVS_409_002")
    void handleDuplicateResourceReturns409() {
        DuplicateResourceException ex =
                new DuplicateResourceException("Film", "title", "Inception");

        ResponseEntity<ApiResponse<Void>> response = handler.handleDuplicateResource(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_409_002");
    }

    @Test
    @DisplayName("handleDataIntegrity returns 409 with EVS_409_003")
    void handleDataIntegrityReturns409() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Unique constraint");

        ResponseEntity<ApiResponse<Void>> response = handler.handleDataIntegrity(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_409_003");
    }

    @Test
    @DisplayName("handleServiceException returns 500 with EVS_500_002")
    void handleServiceExceptionReturns500() {
        ServiceException ex = new ServiceException("Unexpected failure");

        ResponseEntity<ApiResponse<Void>> response = handler.handleServiceException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_500_002");
    }

    @Test
    @DisplayName("handleBadRequest returns 400 with EVS_400_005")
    void handleBadRequestReturns400() {
        BadRequestException ex = new BadRequestException("Bad request", "actor", "actor.bad");

        ResponseEntity<ApiResponse<Void>> response = handler.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_400_005");
    }

    @Test
    @DisplayName("handleEntityNotFound returns 404 with EVS_404_002")
    void handleEntityNotFoundReturns404() {
        EntityNotFoundException ex =
                new EntityNotFoundException("Actor not found", "actor", "actor.notfound");

        ResponseEntity<ApiResponse<Void>> response = handler.handleEntityNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_404_002");
    }

    // ── Validation exceptions ─────────────────────────────────────────────

    @Test
    @DisplayName("handleValidation returns 400 with field errors and EVS_400_001")
    void handleValidationReturns400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        org.springframework.validation.FieldError fieldError =
                new org.springframework.validation.FieldError(
                        "actorDTO", "firstName", null, false, null, null, "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getErrorCount()).thenReturn(1);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_400_001");
        assertThat(response.getBody().getError().getFieldErrors()).hasSize(1);
        assertThat(response.getBody().getError().getFieldErrors().get(0).getField())
                .isEqualTo("firstName");
        assertThat(response.getBody().getError().getFieldErrors().get(0).getMessage())
                .isEqualTo("must not be blank");
    }

    @Test
    @DisplayName("handleMissingParam returns 400 with EVS_400_002")
    void handleMissingParamReturns400() throws Exception {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("page", "Integer");

        ResponseEntity<ApiResponse<Void>> response = handler.handleMissingParam(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_400_002");
        assertThat(response.getBody().getMessage()).contains("page");
    }

    @Test
    @DisplayName("handleTypeMismatch returns 400 with EVS_400_003")
    void handleTypeMismatchReturns400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");
        when(ex.getValue()).thenReturn("abc");
        when(ex.getMessage()).thenReturn("Failed to convert 'abc' to Integer");

        ResponseEntity<ApiResponse<Void>> response = handler.handleTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_400_003");
        assertThat(response.getBody().getMessage()).contains("id");
    }

    // ── Security exceptions ───────────────────────────────────────────────

    @Test
    @DisplayName("handleAuthentication returns 401 with EVS_401_001")
    void handleAuthenticationReturns401() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<ApiResponse<Void>> response = handler.handleAuthentication(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_401_001");
        assertThat(response.getBody().getMessage()).isEqualTo("Authentication required");
    }

    @Test
    @DisplayName("handleAccessDenied returns 403 with EVS_403_001")
    void handleAccessDeniedReturns403() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<ApiResponse<Void>> response = handler.handleAccessDenied(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_403_001");
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
    }

    // ── Catch-all ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("handleGeneral returns 500 with EVS_500_001")
    void handleGeneralReturns500() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EVS_500_001");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    @DisplayName("all error responses have non-null timestamp")
    void allErrorResponsesHaveTimestamp() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Actor", "id", 99);

        ResponseEntity<ApiResponse<Void>> response = handler.handleNotFound(ex, request);

        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}

