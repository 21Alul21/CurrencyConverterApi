package com.api.currencyconverterservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@Service
public class CurrencyAPIService {

    private final WebClient webClient;

    @Value("${currencyApi.access_key}")
    String accessKey;

    public CurrencyAPIService(WebClient webclient){
        this.webClient = webclient;
    }

    public String getCurrencies(){
        String url = "https://api.currencyapi.com/v3/currencies";

        return webClient
        .get()
        .uri(url)
        .header("apikey", accessKey)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }


    public Mono<JsonNode> convertCurrency(BigDecimal value, String to){
        String url = "https://api.currencyapi.com/v3/convert?value=" + String.valueOf(value);
        return webClient
        .get()
        .uri(url)
        .header("apikey", accessKey)
        .retrieve()
        .bodyToMono(JsonNode.class);
    }


    public String rateHistory(){

        LocalDate dateToday = LocalDate.now();
        String url = "https://api.currencyapi.com/v3/historical?date=" + 
        dateToday.toString();

        return webClient
        .get()
        .uri(url)
        .header("apikey", accessKey)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }

}
