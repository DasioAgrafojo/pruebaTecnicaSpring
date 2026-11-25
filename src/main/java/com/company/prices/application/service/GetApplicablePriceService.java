package com.company.prices.application.service;

import com.company.prices.application.port.input.GetApplicablePriceUseCase;
import com.company.prices.application.port.output.LoadPricesPort;
import com.company.prices.domain.exception.PriceNotFoundException;
import com.company.prices.domain.model.Price;
import java.time.LocalDateTime;
import java.util.Objects;

public class GetApplicablePriceService implements GetApplicablePriceUseCase {

    private final LoadPricesPort loadPricesPort;

    public GetApplicablePriceService(LoadPricesPort loadPricesPort) {
        this.loadPricesPort = loadPricesPort;
    }

    @Override
    public Price getApplicablePrice(LocalDateTime applicationDate, Long productId, Long brandId) {
        Objects.requireNonNull(applicationDate, "applicationDate must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(brandId, "brandId must not be null");

        return loadPricesPort.loadPrices(productId, brandId).stream()
                .filter(price -> price.isApplicableAt(applicationDate))
                .max(Price.PRECEDENCE)
                .orElseThrow(() -> new PriceNotFoundException(productId, brandId, applicationDate));
    }
}

