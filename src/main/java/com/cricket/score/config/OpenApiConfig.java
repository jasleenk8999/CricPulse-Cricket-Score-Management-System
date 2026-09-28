package com.cricket.score.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Live Cricket Score Platform API")
                        .version("1.0.0")
                        .description("RESTful API services for managing cricket matches, teams, players, real-time ball-by-ball scoring, player statistics, and match auto-simulation.")
                        .contact(new Contact().name("Cricket Tech Team").email("support@cricketplatform.com"))
                        .license(new License().name("Apache 2.0").url("https://spring.io")));
    }
}
