package com.pandalkai.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecommerceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Ecommerce - Kai Pandal")
                .version("1.0")
                .description("API REST de productos con Spring Boot, MySQL y JWT"));
    }
}
