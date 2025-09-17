package com.api.currencyconverterservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder){
        return builder
        .defaultHeader("Content-Type", "application/json")
        .build();
    }

}
