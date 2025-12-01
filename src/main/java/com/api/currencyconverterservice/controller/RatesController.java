package com.api.currencyconverterservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.api.currencyconverterservice.repository.RecentRateRepository;
import com.api.currencyconverterservice.service.FixerApiService;
import com.api.currencyconverterservice.service.OpenExchangeApiService;
import com.api.currencyconverterservice.service.ResponseService;

import reactor.core.publisher.Mono;

/*
 * - This is the RatesController module.
 * - The RatesController saves the request in the DB and returns the current exchange rates of
 * different supported currencies.
 * 
 * - A base currency symbol has to be passed as request parameter
 * 
 * --------------------------------------------------------------
 *      send GET request to api/v1/rates?base=USD
 * --------------------------------------------------------------
 * 
 * - There is primary and secondary external APIs for a resilient
 * fallback mechanism.
 * 
 * - If both APIs fail, cached results is returned with a warning..
 */

@RestController
@RequestMapping("api/v1")
public class RatesController {
     /**
     * Provides current exchange rates from any of the available external APIs Asynchronously
     * @param base  The base currency (e.g., "USD")
     * @return A JSON output containing the exchange rate of different currencies based on the
     *          specified base in the RequestParameter
     */

    private final ResponseService responseService;
   

    public RatesController(FixerApiService fixerApiService, OpenExchangeApiService openExchangeApiService, RecentRateRepository recentRateRepository,
         ResponseService responseService) {
        this.responseService = responseService;
    }

    @GetMapping("/rates")
    public ResponseEntity<Mono<Object>> getRates(@RequestParam String base) {
    Mono<Object> response = responseService.getRates(base);
    return ResponseEntity.ok(response);
    
   }
}






