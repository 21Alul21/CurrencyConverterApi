package com.api.currencyconverterservice.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

import com.api.currencyconverterservice.service.CurrencyAPIService;
import com.api.currencyconverterservice.service.FixerApiService;

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

    private final FixerApiService fixerApiService;
    private final CurrencyAPIService currencyAPIService;

    public RateHistoryController(FixerApiService fixerApiService, 
    CurrencyAPIService currencyAPIService){
        this.fixerApiService = fixerApiService;
        this.currencyAPIService = currencyAPIService;
    }

    @GetExchange("/health")
    public String rateHistory(){

        try{
            String fixerApiResponse = fixerApiService.rateHistory();
            return fixerApiResponse;
        }catch (Exception e){
            System.out.println("an error occured while retrieving data from the fixer API: " 
            + e.getMessage());
        }

        try{
           String currencyAPIResponse = currencyAPIService.rateHistory();
           return currencyAPIResponse;
        }catch (Exception e){
            System.out.println("an error occured while retrieving data from the CurrencyAPI: " 
            + e.getMessage());
        }


        


        return null;
        
    }

}
