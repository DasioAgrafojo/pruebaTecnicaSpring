package com.company.prices.infrastructure.adapter.output.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPriceRepository extends JpaRepository<PriceJpaEntity, Long> {

    List<PriceJpaEntity> findByProductIdAndBrandId(Long productId, Long brandId);
}

