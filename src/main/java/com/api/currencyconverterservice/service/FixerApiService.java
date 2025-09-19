package com.api.currencyconverterservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class FixerApiService {
    private final WebClient webClient;

    @Value("${fixerApi.access_key}")
    String accessKey;

    public FixerApiService(WebClient webClient){
        this.webClient = webClient;
    }

    public String getCurrencies(){

     String url = "http://data.fixer.io/api/symbols?access_key=" + accessKey;

    
        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }
    
}

