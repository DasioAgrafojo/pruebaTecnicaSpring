package com.company.prices.infrastructure.adapter.input.rest;

import com.company.prices.application.port.input.GetApplicablePriceUseCase;
import com.company.prices.infrastructure.adapter.input.rest.dto.PriceResponse;
import com.company.prices.infrastructure.adapter.input.rest.mapper.PriceResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/prices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Prices", description = "Price lookup")
public class PriceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceController.class);

    private final GetApplicablePriceUseCase getApplicablePriceUseCase;
    private final PriceResponseMapper mapper;

    public PriceController(GetApplicablePriceUseCase getApplicablePriceUseCase, PriceResponseMapper mapper) {
        this.getApplicablePriceUseCase = getApplicablePriceUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Get the applicable price for a product, brand and date",
            description = "When several prices overlap, the one with the highest priority is returned.")
    @ApiResponse(responseCode = "200", description = "Price found")
    @ApiResponse(responseCode = "400", description = "Missing or invalid parameter",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No applicable price",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping
    public PriceResponse getApplicablePrice(
            @Parameter(description = "Application date and time, ISO-8601 without offset",
                    example = "2020-06-14T16:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @Parameter(description = "Product identifier", example = "35455")
            @RequestParam @Positive Long productId,
            @Parameter(description = "Brand identifier", example = "1")
            @RequestParam @Positive Long brandId) {
        LOGGER.info("Received price request: productId={}, brandId={}, applicationDate={}",
                productId, brandId, applicationDate);
        return mapper.toResponse(getApplicablePriceUseCase.getApplicablePrice(applicationDate, productId, brandId));
    }
}

