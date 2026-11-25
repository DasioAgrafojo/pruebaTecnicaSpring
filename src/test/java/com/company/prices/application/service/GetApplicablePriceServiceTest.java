package com.company.prices.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.company.prices.application.port.output.LoadPricesPort;
import com.company.prices.domain.exception.PriceNotFoundException;
import com.company.prices.domain.model.Price;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetApplicablePriceServiceTest {

    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;
    private static final LocalDateTime DATE = LocalDateTime.parse("2020-06-14T16:00:00");

    @Mock
    private LoadPricesPort loadPricesPort;

    @InjectMocks
    private GetApplicablePriceService service;

    @Test
    void returnsSinglePrice() {
        Price price = price(1L, 0, "35.50", "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        when(loadPricesPort.loadPrices(PRODUCT_ID, BRAND_ID)).thenReturn(List.of(price));

        assertThat(service.getApplicablePrice(DATE, PRODUCT_ID, BRAND_ID)).isEqualTo(price);
    }

    @Test
    void returnsHighestPriorityAmongOverlappingPrices() {
        Price low = price(1L, 0, "35.50", "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        Price high = price(2L, 1, "25.45", "2020-06-14T15:00:00", "2020-06-14T18:30:00");
        Price middle = price(3L, 0, "30.50", "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        when(loadPricesPort.loadPrices(PRODUCT_ID, BRAND_ID)).thenReturn(List.of(low, high, middle));

        assertThat(service.getApplicablePrice(DATE, PRODUCT_ID, BRAND_ID)).isEqualTo(high);
    }

    @Test
    void breaksTiesUsingHighestPriceList() {
        Price first = price(2L, 1, "25.45", "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        Price second = price(3L, 1, "30.50", "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        when(loadPricesPort.loadPrices(PRODUCT_ID, BRAND_ID)).thenReturn(List.of(second, first));

        assertThat(service.getApplicablePrice(DATE, PRODUCT_ID, BRAND_ID)).isEqualTo(second);
    }

    @Test
    void ignoresPricesNotApplicableAtDate() {
        Price expiredHighPriority = price(2L, 5, "25.45", "2020-06-14T00:00:00", "2020-06-14T15:59:59");
        Price applicable = price(1L, 0, "35.50", "2020-06-14T00:00:00", "2020-12-31T23:59:59");
        when(loadPricesPort.loadPrices(PRODUCT_ID, BRAND_ID)).thenReturn(List.of(expiredHighPriority, applicable));

        assertThat(service.getApplicablePrice(DATE, PRODUCT_ID, BRAND_ID)).isEqualTo(applicable);
    }

    @Test
    void boundariesAreInclusive() {
        Price price = price(2L, 1, "25.45", "2020-06-14T16:00:00", "2020-06-14T16:00:00");
        when(loadPricesPort.loadPrices(PRODUCT_ID, BRAND_ID)).thenReturn(List.of(price));

        assertThat(service.getApplicablePrice(DATE, PRODUCT_ID, BRAND_ID)).isEqualTo(price);
    }

    @Test
    void throwsWhenNoPriceApplies() {
        when(loadPricesPort.loadPrices(PRODUCT_ID, BRAND_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.getApplicablePrice(DATE, PRODUCT_ID, BRAND_ID))
                .isInstanceOf(PriceNotFoundException.class);
    }

    @Test
    void rejectsNullInputs() {
        assertThatThrownBy(() -> service.getApplicablePrice(null, PRODUCT_ID, BRAND_ID))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.getApplicablePrice(DATE, null, BRAND_ID))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.getApplicablePrice(DATE, PRODUCT_ID, null))
                .isInstanceOf(NullPointerException.class);
    }

    private static Price price(Long priceList, int priority, String amount, String start, String end) {
        return new Price(BRAND_ID, PRODUCT_ID, priceList,
                LocalDateTime.parse(start), LocalDateTime.parse(end),
                priority, new BigDecimal(amount), Currency.getInstance("EUR"));
    }
}

