package com.api.currencyconverterservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@Service
public class CurrencyAPIService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyAPIService.class);

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


    // @Cacheable(value = "currencyConversions", key = "#value + '-' + #to")
    public Mono<JsonNode> convertCurrency(BigDecimal value, String to){
        String url = "https://api.currencyapi.com/v3/convert?value=" + String.valueOf(value);
       
       logger.info("in the currencyAPI service, trying to call the convert external api");
       logger.info("fixer api url: " + url);


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