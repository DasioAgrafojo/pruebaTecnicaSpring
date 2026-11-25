package com.company.prices.infrastructure.adapter.input.rest;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerIntegrationTest {

    private static final String URL = "/api/v1/prices";

    @Autowired
    private MockMvc mockMvc;

    static Stream<Arguments> mandatoryCases() {
        return Stream.of(
                Arguments.of("2020-06-14T10:00:00", 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59", "35.50"),
                Arguments.of("2020-06-14T16:00:00", 2, "2020-06-14T15:00:00", "2020-06-14T18:30:00", "25.45"),
                Arguments.of("2020-06-14T21:00:00", 1, "2020-06-14T00:00:00", "2020-12-31T23:59:59", "35.50"),
                Arguments.of("2020-06-15T10:00:00", 3, "2020-06-15T00:00:00", "2020-06-15T11:00:00", "30.50"),
                Arguments.of("2020-06-16T21:00:00", 4, "2020-06-15T16:00:00", "2020-12-31T23:59:59", "38.95"));
    }

    @ParameterizedTest(name = "date {0} -> price list {1}")
    @MethodSource("mandatoryCases")
    void returnsExpectedPrice(String date, int priceList, String start, String end, String price) throws Exception {
        perform(date, "35455", "1")
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(priceList))
                .andExpect(jsonPath("$.startDate").value(start))
                .andExpect(jsonPath("$.endDate").value(end))
                .andExpect(jsonPath("$.price").value(comparesEqualTo(new BigDecimal(price).doubleValue())))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void boundaryDatesAreInclusive() throws Exception {
        perform("2020-06-14T18:30:00", "35455", "1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(2));
        perform("2020-06-14T18:30:01", "35455", "1")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(1));
    }

    @Test
    void returnsNotFoundWhenNoPriceApplies() throws Exception {
        perform("2020-06-14T10:00:00", "99999", "1")
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Price not found"))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.instance").value(URL))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
        perform("2019-01-01T00:00:00", "35455", "1").andExpect(status().isNotFound());
    }

    @Test
    void returnsBadRequestForInvalidDate() throws Exception {
        perform("not-a-date", "35455", "1")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.instance").value(URL))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void returnsBadRequestForMissingParameters() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isBadRequest());
        mockMvc.perform(get(URL).param("applicationDate", "2020-06-14T10:00:00").param("productId", "35455"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestForInvalidIdentifiers() throws Exception {
        perform("2020-06-14T10:00:00", "abc", "1").andExpect(status().isBadRequest());
        perform("2020-06-14T10:00:00", "35455", "xyz").andExpect(status().isBadRequest());
        perform("2020-06-14T10:00:00", "-1", "1").andExpect(status().isBadRequest());
        perform("2020-06-14T10:00:00", "35455", "0").andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundForUnknownRoute() throws Exception {
        mockMvc.perform(get("/api/v1/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void returnsMethodNotAllowedForUnsupportedMethod() throws Exception {
        mockMvc.perform(post(URL))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    private ResultActions perform(String date, String productId, String brandId) throws Exception {
        return mockMvc.perform(get(URL)
                .param("applicationDate", date)
                .param("productId", productId)
                .param("brandId", brandId));
    }
}

