package com.company.prices.infrastructure.adapter.input.rest.mapper;

import com.company.prices.domain.model.Price;
import com.company.prices.infrastructure.adapter.input.rest.dto.PriceResponse;
import org.springframework.stereotype.Component;

@Component
public class PriceResponseMapper {

    public PriceResponse toResponse(Price price) {
        return new PriceResponse(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.amount(),
                price.currency().getCurrencyCode());
    }
}

