package com.api.currencyconverterservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.currencyconverterservice.service.FixerApiService;
import com.api.currencyconverterservice.service.OpenExchangeApiService;

import reactor.core.publisher.Mono;

/*
 * - This is the RatesController module.
 * - The RatesController returns the current exchange rates of
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
 * 
 */

@RestController
@RequestMapping("api/v1")
public class RatesController {

    private final FixerApiService fixerApiService;
    private final OpenExchangeApiService openExchangeApiService;

    public RatesController(FixerApiService fixerApiService, OpenExchangeApiService 
    openExchangeApiService){
        this.fixerApiService = fixerApiService;
        this.openExchangeApiService = openExchangeApiService;
    }


    @GetMapping("/rates")
    public Mono<String> getRates(@RequestParam String base){

        Mono<String> fixerResponse =  fixerApiService.getRates(base);
        Mono<String> openExchangeResponse = openExchangeApiService.getRates(base);
        

        return Mono.zip(fixerResponse.defaultIfEmpty(null), openExchangeResponse
        .defaultIfEmpty(null))
        .map(tuple -> {
            String fixerTupple = tuple.getT1();
            String openExchangeTupple = tuple.getT2();

            if (fixerTupple != null){
                return fixerTupple;
            }

            else if(openExchangeTupple != null){
                return openExchangeTupple;
            }

            else{
            return  null;} 
        });


    }
}
