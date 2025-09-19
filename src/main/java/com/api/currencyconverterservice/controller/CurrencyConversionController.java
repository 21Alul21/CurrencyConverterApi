package com.api.currencyconverterservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("api/v1")
public class CurrencyConversionController {
    private final WebClient webClient;

    @Value("${fixerApi.access_key}")
    String accessKey;

    @Value("${openexchangeApi.access_key}")
    String accessKey2;

    @Value("${curreencyApi.access_key}")
    String accessKey3;

     public CurrencyConversionController(WebClient webClient){
        this.webClient = webClient;
    }

    @GetMapping("/convert")
    public String convertCurrency(){

    }

}
