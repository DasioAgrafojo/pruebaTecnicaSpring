package com.company.prices.infrastructure.adapter.output.persistence;

import com.company.prices.domain.model.Price;
import java.util.Currency;
import org.springframework.stereotype.Component;

@Component
public class PricePersistenceMapper {

    public Price toDomain(PriceJpaEntity entity) {
        return new Price(
                entity.getBrandId(),
                entity.getProductId(),
                entity.getPriceList(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPriority(),
                entity.getPrice(),
                Currency.getInstance(entity.getCurrency()));
    }
}

