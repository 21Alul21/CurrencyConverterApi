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
public class FixerApiService {
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(FixerApiService.class);

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

         logger.info("in the fixer api service trying to call the convert external api");
         logger.info("fixer api url: " + url);

         
        return webClient
        .get()
        .uri(url)
        .retrieve()
        .onStatus(
            status -> status.is5xxServerError(),
            clientResponse -> clientResponse.bodyToMono(String.class)
                .doOnNext(body -> logger.warn("Fixer API returned 5xx error: " + body))
                .then(Mono.error(new RuntimeException("Fixer API 5xx error")))
        )
        .bodyToMono(JsonNode.class)
        .onErrorResume(e -> {
                logger.warn("Fixer API has failed: " + e.getMessage());
                return Mono.empty();
            });
    }


    public String rateHistory(){

        LocalDate dateToday = LocalDate.now(); 
        String url = "http://data.fixer.io/api/" + dateToday.toString() + 
        "?access_key=" + accessKey + "&base=USD";

        logger.info(url);
        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }


    public Mono<String> getRates(String base){

        String url = "http://data.fixer.io/api/latest?access_key="
         + accessKey + "&base=" + base ;


         logger.info(url);

        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .onErrorResume(e ->{
            logger.warn("an error occured while fetching data from the fixer API, Switching to OpenExchange API" 
            + e.getMessage());
            return Mono.empty();
        });
    }
    
}
