package com.company.prices.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI pricesOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Prices API")
                .description("Retrieves the applicable product price for a brand at a given date")
                .version("v1"));
    }
}

