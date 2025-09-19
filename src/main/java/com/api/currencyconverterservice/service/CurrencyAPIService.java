package com.api.currencyconverterservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CurrencyAPIService {

    private final WebClient webClient;

    @Value("${curreencyApi.access_key}")
    String accessKey;

    public CurrencyAPIService(WebClient webclient){
        this.webClient = webclient;
    }

    public String getCurrencies(){
        String url = "" + accessKey;

        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }

}
