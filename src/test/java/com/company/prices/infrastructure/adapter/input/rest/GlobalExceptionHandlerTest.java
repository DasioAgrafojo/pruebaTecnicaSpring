package com.company.prices.infrastructure.adapter.input.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.prices.domain.exception.PriceNotFoundException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final ServletWebRequest request =
            new ServletWebRequest(new MockHttpServletRequest("GET", "/api/v1/prices"));

    @Test
    void unexpectedExceptionReturnsProblemWithoutLeakingDetails() {
        ResponseEntity<Object> response = handler.handleUnexpected(new IllegalStateException("secret"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetail problem = (ProblemDetail) response.getBody();
        assertThat(problem).isNotNull();
        assertThat(problem.getStatus()).isEqualTo(500);
        assertThat(problem.getTitle()).isEqualTo("Internal server error");
        assertThat(problem.getDetail()).doesNotContain("secret");
        assertThat(problem.getProperties()).containsKey("timestamp");
    }

    @Test
    void priceNotFoundReturnsProblemWithNotFoundStatus() {
        ResponseEntity<Object> response = handler.handlePriceNotFound(
                new PriceNotFoundException(35455L, 1L, LocalDateTime.parse("2020-06-14T10:00:00")), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ProblemDetail problem = (ProblemDetail) response.getBody();
        assertThat(problem).isNotNull();
        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getDetail()).contains("35455");
        assertThat(problem.getProperties()).containsKey("timestamp");
    }
}

