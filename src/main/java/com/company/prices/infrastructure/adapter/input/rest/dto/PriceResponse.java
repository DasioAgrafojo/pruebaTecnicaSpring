package com.company.prices.infrastructure.adapter.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Applicable price")
public record PriceResponse(
        @Schema(description = "Product identifier", example = "35455") Long productId,
        @Schema(description = "Brand identifier", example = "1") Long brandId,
        @Schema(description = "Price list to apply", example = "2") Long priceList,
        @Schema(description = "Start of validity (inclusive)", example = "2020-06-14T15:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startDate,
        @Schema(description = "End of validity (inclusive)", example = "2020-06-14T18:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endDate,
        @Schema(description = "Final price", example = "25.45") BigDecimal price,
        @Schema(description = "ISO-4217 currency code", example = "EUR") String currency) {
}

