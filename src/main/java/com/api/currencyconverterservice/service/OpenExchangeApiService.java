package com.api.currencyconverterservice.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class OpenExchangeApiService {

    private final WebClient webClient;

    @Value("${openexchangeApi.access_key}")
    String accessKey;

    public OpenExchangeApiService(WebClient webClient){
        this.webClient = webClient;
    }

    public String getCurrencies(){
        String url = "https://openexchangerates.org/api/currencies.json?app_id=" + accessKey;
        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }
    
}

