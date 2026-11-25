package com.company.prices.application.port.output;

import com.company.prices.domain.model.Price;
import java.util.List;

public interface LoadPricesPort {

    /**
     * Loads every price of the product and brand. Deciding which one applies is a domain concern.
     */
    List<Price> loadPrices(Long productId, Long brandId);
}

