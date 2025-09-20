package com.api.currencyconverterservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.currencyconverterservice.service.FixerApiService;
import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("api/v1")
public class RatesController {
    private Logger logger = LoggerFactory.getLogger(RatesController.class);
    private final FixerApiService fixerApiService;

    public RatesController(FixerApiService fixerApiService){
        this.fixerApiService = fixerApiService;
    }


    @GetMapping("/rates")
    public String getRates(@RequestParam String base){

        Mono<JsonNode> fixerResponse =  fixerApiService.getRates(base)
        .onErrorResume(e ->{
            logger.error("an error occured while fetching data from the fixer API" 
            + e.getMessage());
            return Mono.empty();
        });


    }
}
