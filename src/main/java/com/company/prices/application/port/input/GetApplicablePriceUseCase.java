package com.company.prices.application.port.input;

import com.company.prices.domain.model.Price;
import java.time.LocalDateTime;

public interface GetApplicablePriceUseCase {

    /**
     * @throws com.company.prices.domain.exception.PriceNotFoundException if no price applies
     */
    Price getApplicablePrice(LocalDateTime applicationDate, Long productId, Long brandId);
}

