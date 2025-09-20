package com.api.currencyconverterservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

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


    public Mono<JsonNode> convertCurrency(String from, String to, BigDecimal amount){
        String url = "http://data.fixer.io/api/convert?access_key="
         + accessKey + "&from=" + from + "&to=" + to + "&amount=" + String.valueOf(amount);

         System.out.println("fixer api url: " + url);
         
        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(JsonNode.class);
    }


    public String rateHistory(){

        LocalDate dateToday = LocalDate.now(); 
        String url = "http://data.fixer.io/api/" + dateToday.toString() + 
        "?access_key=" + accessKey + "&base=USD";
        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }
    
}
