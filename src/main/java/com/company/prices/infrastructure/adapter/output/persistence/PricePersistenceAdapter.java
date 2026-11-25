package com.company.prices.infrastructure.adapter.output.persistence;

import com.company.prices.application.port.output.LoadPricesPort;
import com.company.prices.domain.model.Price;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PricePersistenceAdapter implements LoadPricesPort {

    private final SpringDataPriceRepository repository;
    private final PricePersistenceMapper mapper;

    public PricePersistenceAdapter(SpringDataPriceRepository repository, PricePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Price> loadPrices(Long productId, Long brandId) {
        return repository.findByProductIdAndBrandId(productId, brandId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

