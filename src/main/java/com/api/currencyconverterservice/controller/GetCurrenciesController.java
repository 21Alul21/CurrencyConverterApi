package com.api.currencyconverterservice.controller;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.currencyconverterservice.service.GetCurrencyCircuitBreakerService;

import reactor.core.publisher.Mono;



/*This module contains the controller for getting list 
 * of supported currencies and their symbols from external APIs
 * through a robust fall back mechanism: if one fails, it utilizes
 * another available API.
 * 
 * --------------------------------------------------------
 *    send GET request to the api/v1/currencies endpoint
 * --------------------------------------------------------
 * 
 */

@RestController
@RequestMapping("api/v1")
public class GetCurrenciesController {


    private final GetCurrencyCircuitBreakerService getCurrencyCircuitBreakerService;

    public GetCurrenciesController(GetCurrencyCircuitBreakerService getCurrencyCircuitBreakerService){
        this.getCurrencyCircuitBreakerService = getCurrencyCircuitBreakerService;
    }

    @GetMapping("/currencies")
    public ResponseEntity<Mono<String>> getCurrencies(){

    /**
     * provides supported currencies for conversion
     * @return A JSON of the of supported currencies and their symbols
     */
     return ResponseEntity.ok(getCurrencyCircuitBreakerService
                          .fixerAPIGetCurrencies());
    }
}
