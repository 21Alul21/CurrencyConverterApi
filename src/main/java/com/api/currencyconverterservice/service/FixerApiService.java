package com.api.currencyconverterservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class FixerApiService {
    private final WebClient webClient;

    public FixerApiService(WebClient webClient){
        this.webClient = webClient;
    }

    public String getCurrencies(){
    String url = "http://data.fixer.io/api/symbols?access_key=0c46424a5ed58f018440ede330c10e41";
        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }
    
}
