package com.api.currencyconverterservice.service;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;



@Service
public class OpenExchangeApiService {
    private static final Logger logger = LoggerFactory.getLogger(OpenExchangeApiService.class);

    private final WebClient webClient;

    @Value("${openexchangeApi.access_key}")
    String accessKey;

    public OpenExchangeApiService(WebClient webClient){
        this.webClient = webClient;
    }

    public String getCurrencies(){
        String url = "https://openexchangerates.org/api/currencies.json?app_id="
         + accessKey;
        return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }


    public Mono<String> getRates(String base ){
        String url = "https://openexchangerates.org/api/latest.json?app_id="
         + accessKey + "base=" + base;

         return webClient
         .get()
         .uri(url)
         .retrieve()
         .bodyToMono(String.class)
         .onErrorResume(e -> {
            logger.error("an error occured while connecting to the OpenExchange external API: "
            + e.getMessage());
            return Mono.just("");
        });
    }
    
}

