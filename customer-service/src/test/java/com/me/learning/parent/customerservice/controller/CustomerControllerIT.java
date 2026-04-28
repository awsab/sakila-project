package com.me.learning.parent.customerservice.controller;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.customerservice.dto.AddressResponse;
import com.me.learning.parent.customerservice.dto.CustomerRequest;
import com.me.learning.parent.customerservice.dto.CustomerDetailResponse;
import com.me.learning.parent.customerservice.dto.CustomerResponse;
import com.me.learning.parent.customerservice.dto.CustomerUpdateRequest;
import com.me.learning.parent.customerservice.dto.PaymentDetailResponse;
import com.me.learning.parent.customerservice.service.CustomerService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link CustomerController} using REST Assured.
 * The Spring context is loaded with a random port; the service layer is mocked
 * so no real database is required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("CustomerController Integration Tests")
class CustomerControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private CustomerService customerService;

    private static final String BASE_URL = "/api/v1/customers";

    private CustomerResponse sampleResponse;
    private CustomerDetailResponse sampleDetailResponse;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        sampleResponse = new CustomerResponse(
                1, (short) 1, "MARY", "SMITH", "mary.smith@sakilacustomer.org",
                5, true, Instant.parse("2006-02-14T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z"));

        AddressResponse addressResponse = new AddressResponse(
                5, "123 Main St", null, "DISTRICT", 2, "CITY", "12345", "+123456789", Instant.parse("2026-01-01T00:00:00Z"));
        PaymentDetailResponse paymentDetailResponse = new PaymentDetailResponse(
                9, (short) 3, new BigDecimal("12.34"), Instant.parse("2026-01-15T00:00:00Z"),
                Instant.parse("2026-01-15T00:00:00Z"));
        sampleDetailResponse = new CustomerDetailResponse(
                1, (short) 1, "MARY", "SMITH", "mary.smith@sakilacustomer.org",
                5, addressResponse, List.of(paymentDetailResponse), true,
                Instant.parse("2006-02-14T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/v1/customers
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /api/v1/customers — createCustomer")
    class CreateCustomer {

        @Test
        @DisplayName("should return 201 and the created customer")
        void createCustomer_returnsCreated() {
            when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(sampleResponse);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"storeId\":1,\"firstName\":\"MARY\",\"lastName\":\"SMITH\","
                            + "\"email\":\"mary.smith@sakilacustomer.org\",\"addressId\":5,"
                            + "\"active\":true,\"createDate\":\"2006-02-14T00:00:00Z\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(201)
                    .body("id",        equalTo(1))
                    .body("firstName", equalTo("MARY"))
                    .body("lastName",  equalTo("SMITH"));
        }

        @Test
        @DisplayName("should return 400 when firstName is blank")
        void createCustomer_blankFirstName_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"storeId\":1,\"firstName\":\"\",\"lastName\":\"SMITH\","
                            + "\"email\":\"mary.smith@sakilacustomer.org\",\"addressId\":5,"
                            + "\"active\":true,\"createDate\":\"2006-02-14T00:00:00Z\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when lastName is blank")
        void createCustomer_blankLastName_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"storeId\":1,\"firstName\":\"MARY\",\"lastName\":\"\","
                            + "\"email\":\"mary.smith@sakilacustomer.org\",\"addressId\":5,"
                            + "\"active\":true,\"createDate\":\"2006-02-14T00:00:00Z\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when storeId is null")
        void createCustomer_nullStoreId_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"firstName\":\"MARY\",\"lastName\":\"SMITH\","
                            + "\"email\":\"mary.smith@sakilacustomer.org\",\"addressId\":5,"
                            + "\"active\":true,\"createDate\":\"2006-02-14T00:00:00Z\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when email already in use")
        void createCustomer_duplicateEmail_returns400() {
            when(customerService.createCustomer(any(CustomerRequest.class)))
                    .thenThrow(new IllegalArgumentException("Email already in use"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"storeId\":1,\"firstName\":\"MARY\",\"lastName\":\"SMITH\","
                            + "\"email\":\"mary.smith@sakilacustomer.org\",\"addressId\":5,"
                            + "\"active\":true,\"createDate\":\"2006-02-14T00:00:00Z\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUT /api/v1/customers/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PUT /api/v1/customers/{id} — updateCustomer")
    class UpdateCustomer {

        @Test
        @DisplayName("should return 200 and the updated customer")
        void updateCustomer_returnsOk() {
            CustomerResponse updated = new CustomerResponse(
                    1, (short) 1, "MARY", "JONES", "mary.jones@sakilacustomer.org",
                    5, true, null, null);
            when(customerService.updateCustomer(eq(1), any(CustomerUpdateRequest.class))).thenReturn(updated);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"storeId\":1,\"firstName\":\"MARY\",\"lastName\":\"JONES\","
                            + "\"email\":\"mary.jones@sakilacustomer.org\","
                            + "\"addressId\":5,\"active\":true}")
            .when()
                    .put(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("lastName", equalTo("JONES"));
        }

        @Test
        @DisplayName("should return 404 when customer not found")
        void updateCustomer_notFound_returns404() {
            when(customerService.updateCustomer(eq(99), any(CustomerUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Customer not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"storeId\":1,\"firstName\":\"MARY\",\"lastName\":\"JONES\","
                            + "\"email\":\"mary.jones@sakilacustomer.org\","
                            + "\"addressId\":5,\"active\":true}")
            .when()
                    .put(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PATCH /api/v1/customers/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PATCH /api/v1/customers/{id} — patchCustomer")
    class PatchCustomer {

        @Test
        @DisplayName("should return 200 and the patched customer")
        void patchCustomer_returnsOk() {
            CustomerResponse patched = new CustomerResponse(
                    1, (short) 1, "MARY", "SMITH", "mary.new@sakilacustomer.org",
                    5, false, null, null);
            when(customerService.patchCustomer(eq(1), any(CustomerUpdateRequest.class))).thenReturn(patched);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"storeId\":1,\"firstName\":\"MARY\",\"lastName\":\"SMITH\","
                            + "\"email\":\"mary.new@sakilacustomer.org\","
                            + "\"addressId\":5,\"active\":false}")
            .when()
                    .patch(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("email",  equalTo("mary.new@sakilacustomer.org"))
                    .body("active", is(false));
        }

        @Test
        @DisplayName("should return 404 when customer not found")
        void patchCustomer_notFound_returns404() {
            when(customerService.patchCustomer(eq(99), any(CustomerUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Customer not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"storeId\":1,\"firstName\":\"MARY\",\"lastName\":\"SMITH\","
                            + "\"email\":\"mary.new@sakilacustomer.org\","
                            + "\"addressId\":5,\"active\":false}")
            .when()
                    .patch(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/customers/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/customers/{id} — getCustomerById")
    class GetCustomerById {

        @Test
        @DisplayName("should return 200 and the customer")
        void getCustomerById_found_returnsOk() {
            when(customerService.getCustomerById(1)).thenReturn(sampleDetailResponse);

            given()
            .when()
                    .get(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("id",        equalTo(1))
                    .body("firstName", equalTo("MARY"))
                    .body("lastName",  equalTo("SMITH"))
                    .body("address.id", equalTo(5))
                    .body("address.address", equalTo("123 Main St"))
                    .body("payments", hasSize(1))
                    .body("payments[0].id", equalTo(9))
                    .body("payments[0].amount", equalTo(12.34f));
        }

        @Test
        @DisplayName("should return 404 when customer not found")
        void getCustomerById_notFound_returns404() {
            when(customerService.getCustomerById(99))
                    .thenThrow(new ResourceNotFoundException("Customer not found"));

            given()
            .when()
                    .get(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/customers  (paginated)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/customers — getAllCustomers (paged)")
    class GetAllCustomersPaged {

        @Test
        @DisplayName("should return 200 with paginated content")
        void getAllCustomers_returnsPage() {
            when(customerService.getAllCustomers(any()))
                    .thenReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content",              hasSize(1))
                    .body("content[0].firstName", equalTo("MARY"));
        }

        @Test
        @DisplayName("should return 200 with empty page")
        void getAllCustomers_empty_returnsEmptyPage() {
            when(customerService.getAllCustomers(any())).thenReturn(new PageImpl<>(List.of()));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/customers/active
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/customers/active — getActiveCustomers")
    class GetActiveCustomers {

        @Test
        @DisplayName("should return 200 with active customers")
        void getActiveCustomers_returnsList() {
            when(customerService.getActiveCustomers()).thenReturn(List.of(sampleResponse));

            given()
            .when()
                    .get(BASE_URL + "/active")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].active", is(true));
        }

        @Test
        @DisplayName("should return 200 with empty list when no active customers")
        void getActiveCustomers_empty_returnsEmpty() {
            when(customerService.getActiveCustomers()).thenReturn(List.of());

            given()
            .when()
                    .get(BASE_URL + "/active")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/customers/by-store?storeId=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/customers/by-store — getCustomersByStore")
    class GetCustomersByStore {

        @Test
        @DisplayName("should return 200 with customers for the given store")
        void getCustomersByStore_returnsList() {
            when(customerService.getCustomersByStoreId((short) 1)).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("storeId", 1)
            .when()
                    .get(BASE_URL + "/by-store")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].storeId", equalTo(1));
        }

        @Test
        @DisplayName("should return 200 with empty list when no customers for store")
        void getCustomersByStore_empty_returnsEmpty() {
            when(customerService.getCustomersByStoreId((short) 99)).thenReturn(List.of());

            given()
                    .queryParam("storeId", 99)
            .when()
                    .get(BASE_URL + "/by-store")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/customers/search?lastName=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/customers/search — searchByLastName")
    class SearchByLastName {

        @Test
        @DisplayName("should return 200 with matching customers")
        void searchByLastName_returnsResults() {
            when(customerService.searchByLastName("SMITH")).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("lastName", "SMITH")
            .when()
                    .get(BASE_URL + "/search")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].lastName", equalTo("SMITH"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no match")
        void searchByLastName_noMatch_returnsEmpty() {
            when(customerService.searchByLastName("ZZZZZ")).thenReturn(List.of());

            given()
                    .queryParam("lastName", "ZZZZZ")
            .when()
                    .get(BASE_URL + "/search")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE /api/v1/customers/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("DELETE /api/v1/customers/{id} — deleteCustomer")
    class DeleteCustomer {

        @Test
        @DisplayName("should return 204 on successful deletion")
        void deleteCustomer_returnsNoContent() {
            doNothing().when(customerService).deleteCustomer(1);

            given()
            .when()
                    .delete(BASE_URL + "/1")
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("should return 404 when customer not found")
        void deleteCustomer_notFound_returns404() {
            doThrow(new ResourceNotFoundException("Customer not found"))
                    .when(customerService).deleteCustomer(99);

            given()
            .when()
                    .delete(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/customers/count
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/customers/count — countCustomers")
    class CountCustomers {

        @Test
        @DisplayName("should return 200 with the total count")
        void countCustomers_returnsCount() {
            when(customerService.countCustomers()).thenReturn(599L);

            given()
            .when()
                    .get(BASE_URL + "/count")
            .then()
                    .statusCode(200)
                    .body(equalTo("599"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/customers/exists/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/customers/exists/{id} — existsById")
    class ExistsById {

        @Test
        @DisplayName("should return 200 true when customer exists")
        void existsById_exists_returnsTrue() {
            when(customerService.existsById(1)).thenReturn(true);

            given()
            .when()
                    .get(BASE_URL + "/exists/1")
            .then()
                    .statusCode(200)
                    .body(is("true"));
        }

        @Test
        @DisplayName("should return 200 false when customer does not exist")
        void existsById_notExists_returnsFalse() {
            when(customerService.existsById(99)).thenReturn(false);

            given()
            .when()
                    .get(BASE_URL + "/exists/99")
            .then()
                    .statusCode(200)
                    .body(is("false"));
        }
    }
}

