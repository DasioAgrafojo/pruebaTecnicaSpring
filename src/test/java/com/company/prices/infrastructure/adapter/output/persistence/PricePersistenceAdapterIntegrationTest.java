package com.company.prices.infrastructure.adapter.output.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({PricePersistenceAdapter.class, PricePersistenceMapper.class})
class PricePersistenceAdapterIntegrationTest {

    @Autowired
    private PricePersistenceAdapter adapter;

    @Autowired
    private SpringDataPriceRepository repository;

    @Test
    void databaseIsInitializedWithFourRecords() {
        assertThat(repository.count()).isEqualTo(4);
    }

    @Test
    void loadsAllPricesOfProductAndBrandAsDomainObjects() {
        var prices = adapter.loadPrices(35455L, 1L);

        assertThat(prices).hasSize(4);
        assertThat(prices).allSatisfy(price -> {
            assertThat(price.productId()).isEqualTo(35455L);
            assertThat(price.brandId()).isEqualTo(1L);
            assertThat(price.currency().getCurrencyCode()).isEqualTo("EUR");
        });
        assertThat(prices).anySatisfy(price -> {
            assertThat(price.priceList()).isEqualTo(2L);
            assertThat(price.amount()).isEqualByComparingTo(new BigDecimal("25.45"));
        });
    }

    @Test
    void returnsEmptyForUnknownProductOrBrand() {
        assertThat(adapter.loadPrices(99999L, 1L)).isEmpty();
        assertThat(adapter.loadPrices(35455L, 2L)).isEmpty();
    }
}

