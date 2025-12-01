package com.api.currencyconverterservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

import com.api.currencyconverterservice.service.CurrencyAPIService;
import com.api.currencyconverterservice.service.FixerApiService;
import com.api.currencyconverterservice.service.RateHistoryCircuitBreakerService;

import reactor.core.publisher.Mono;

/*
 * This module contains the RateHistoryController.
 * The RestController returns currency rates within 24 hrs.
 * 
 * The rates are sourced from external API sources.
 * Results from the primary API source is returned only,
 * but if it fails, it falls back to the secondary endpoint,
 * if that fails too, results from the cache is returned with 
 * a warning. 
 * 
 * --------------------------------------------------- 
 *   send GET request to api/v1/health
 * ---------------------------------------------------
 */

 @RestController
 @RequestMapping("api/v1")
public class RateHistoryController {
   
    private final RateHistoryCircuitBreakerService rateHistoryCircuitBreakerService;
    

    public RateHistoryController(RateHistoryCircuitBreakerService rateHistoryCircuitBreakerService){
        this.rateHistoryCircuitBreakerService = rateHistoryCircuitBreakerService;
    }


    @GetExchange("/health")
    public ResponseEntity<Mono<String>> rateHistory(){

    /**
     * Provides currency rates within the last 24 hours only
     * @return A JSON containing currency rates within 24 hours range
     */

         return ResponseEntity.status(HttpStatus.FOUND)
         .body(rateHistoryCircuitBreakerService.fixerRateHistory());        
    }

}
