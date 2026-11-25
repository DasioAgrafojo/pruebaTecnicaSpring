package com.company.prices.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class PriceTest {

    private static final LocalDateTime START = LocalDateTime.parse("2020-06-14T15:00:00");
    private static final LocalDateTime END = LocalDateTime.parse("2020-06-14T18:30:00");

    @Test
    void boundariesAreInclusive() {
        Price price = price(START, END);

        assertThat(price.isApplicableAt(START)).isTrue();
        assertThat(price.isApplicableAt(END)).isTrue();
    }

    @Test
    void datesOutsideIntervalAreNotApplicable() {
        Price price = price(START, END);

        assertThat(price.isApplicableAt(START.minusSeconds(1))).isFalse();
        assertThat(price.isApplicableAt(END.plusSeconds(1))).isFalse();
    }

    @Test
    void rejectsEndDateBeforeStartDate() {
        assertThatThrownBy(() -> price(END, START)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNegativePriorityAndAmount() {
        assertThatThrownBy(() -> new Price(1L, 35455L, 2L, START, END, -1, new BigDecimal("1.00"),
                Currency.getInstance("EUR"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Price(1L, 35455L, 2L, START, END, 1, new BigDecimal("-0.01"),
                Currency.getInstance("EUR"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void precedenceOrdersByPriorityThenPriceList() {
        Price lowPriorityHighList = withPriorityAndList(0, 9L);
        Price highPriorityLowList = withPriorityAndList(1, 1L);
        Price highPriorityHighList = withPriorityAndList(1, 2L);

        assertThat(Price.PRECEDENCE.compare(highPriorityLowList, lowPriorityHighList)).isPositive();
        assertThat(Price.PRECEDENCE.compare(highPriorityHighList, highPriorityLowList)).isPositive();
    }

    private static Price withPriorityAndList(int priority, Long priceList) {
        return new Price(1L, 35455L, priceList, START, END, priority, new BigDecimal("10.00"),
                Currency.getInstance("EUR"));
    }

    private static Price price(LocalDateTime start, LocalDateTime end) {
        return new Price(1L, 35455L, 2L, start, end, 1, new BigDecimal("25.45"), Currency.getInstance("EUR"));
    }
}

