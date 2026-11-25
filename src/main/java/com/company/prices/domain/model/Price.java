package com.company.prices.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Currency;
import java.util.Objects;

/**
 * Immutable domain object describing the price of a product for a brand during a date range.
 * It owns the business rules: when it applies and how it ranks against other prices.
 */
public record Price(
        Long brandId,
        Long productId,
        Long priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int priority,
        BigDecimal amount,
        Currency currency) {

    /**
     * Higher priority wins; on equal priority the higher price list identifier wins (deterministic).
     */
    public static final Comparator<Price> PRECEDENCE = Comparator
            .comparingInt(Price::priority)
            .thenComparing(Price::priceList);

    public Price {
        Objects.requireNonNull(brandId, "brandId must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(priceList, "priceList must not be null");
        Objects.requireNonNull(startDate, "startDate must not be null");
        Objects.requireNonNull(endDate, "endDate must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("priority must not be negative");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
    }

    /**
     * Both interval bounds are inclusive.
     */
    public boolean isApplicableAt(LocalDateTime applicationDate) {
        return !applicationDate.isBefore(startDate) && !applicationDate.isAfter(endDate);
    }
}

