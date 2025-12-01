package com.api.currencyconverterservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@Service
public class FixerApiService {
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(FixerApiService.class);

    @Value("${fixerApi.access_key}")
    String accessKey;

    public FixerApiService(WebClient webClient){
        this.webClient = webClient;
    }

    public Mono<String> getCurrencies(){
     String url = "http://data.fixer.io/api/symbols?access_key=" + accessKey;
       logger.info(url);

       return webClient
             .get()
             .uri(url)
             .exchangeToMono(response -> response.bodyToMono(String.class))
             .doOnError(e -> logger.error("Fixer API error", e));
            
       
    }


    // checks the cache memory for matching request that have lived 
    // for no more than 5 mins, before calling external API.
    @Cacheable(value = "currencyConversions", key = "#from + '-' + #to + '-' + #amount")
    public Mono<JsonNode> convertCurrency(String from, String to, BigDecimal amount){
        String url = "http://data.fixer.io/api/convert?access_key="
         + accessKey + "&from=" + from + "&to=" + to + "&amount=" + String.valueOf(amount);

         logger.info("in the fixer api service trying to call the convert external api");
         logger.info("fixer api url: " + url);

         
        return webClient
        .get()
        .uri(url)
        .exchangeToMono(response -> response.bodyToMono(JsonNode.class))
        .doOnError(e -> logger.error("currencyConverter API error", e));
        
    }


    public Mono<String> rateHistory(){

        LocalDate dateToday = LocalDate.now(); 
        String url = "http://data.fixer.io/api/" + dateToday.toString() + 
        "?access_key=" + accessKey + "&base=USD";

        logger.info(url);
        return webClient
        .get()
        .uri(url)
        .exchangeToMono(response -> response.bodyToMono(String.class))
        .doOnError(e -> logger.error("fixer API failure", e));
    }


    public Mono<String> getRates(String base){

        String url = "http://data.fixer.io/api/latest?access_key="
         + accessKey + "&base=" + base ;


         logger.info("fixer API URL {}", url);

        return webClient
        .get()
        .uri(url)
        .exchangeToMono(response -> response.bodyToMono(String.class))
        .doOnError(e -> logger.error("an error occured while connecting to the Fixer external API", e));
    }
    
}
