package com.company.prices.infrastructure.configuration;

import com.company.prices.application.port.input.GetApplicablePriceUseCase;
import com.company.prices.application.port.output.LoadPricesPort;
import com.company.prices.application.service.GetApplicablePriceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public GetApplicablePriceUseCase getApplicablePriceUseCase(LoadPricesPort loadPricesPort) {
        return new GetApplicablePriceService(loadPricesPort);
    }
}

