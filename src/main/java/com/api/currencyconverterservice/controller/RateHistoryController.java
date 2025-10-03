package com.api.currencyconverterservice.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    private static final Logger logger =  LoggerFactory
    .getLogger(RateHistoryController.class);

    private final FixerApiService fixerApiService;
    private final CurrencyAPIService currencyAPIService;

    public RateHistoryController(FixerApiService fixerApiService, 
    CurrencyAPIService currencyAPIService){
        this.fixerApiService = fixerApiService;
        this.currencyAPIService = currencyAPIService;
    }

    @GetExchange("/health")
    public ResponseEntity<Object> rateHistory(){

        try{
            String fixerApiResponse = fixerApiService.rateHistory();
            return ResponseEntity.status(HttpStatus.FOUND).body(fixerApiResponse);
        }catch (Exception e){
            logger.error("an error occured while retrieving data from the fixer API: " 
            + e.getMessage());
        }

        try{
           String currencyAPIResponse = currencyAPIService.rateHistory();
           return ResponseEntity.status(HttpStatus.FOUND).body(currencyAPIResponse);
        }catch (Exception e){
            logger.error("an error occured while retrieving data from the CurrencyAPI: " 
            + e.getMessage());
        }

        HashMap<String, Object> errHashMap = new HashMap<>();
        errHashMap.put("warning", "both external APIs have failed, please try again later");
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errHashMap);
        
    }

}
