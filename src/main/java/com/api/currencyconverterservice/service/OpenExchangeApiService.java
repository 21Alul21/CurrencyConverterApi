package com.api.currencyconverterservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OpenExchangeApiService {

    private final WebClient webClient;

    public OpenExchangeApiService(WebClient webClient){
        this.webClient = webClient;
    }

    public String getCurrencies(){
        String url = "https://openexchangerates.org/api/currencies.json?app_id=d59906d5f9e142bcb36f75146ef54ec5";
        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }
}
