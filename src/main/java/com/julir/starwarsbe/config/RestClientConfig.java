package com.julir.starwarsbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    public static final String SWAPI_URL= "https://www.swapi.tech/api";

    @Bean
    public RestClient swapiClient() {
        return RestClient.builder()
                .baseUrl(SWAPI_URL)
                .build();
    }
}