package com.api.currencyconverterservice.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.currencyconverterservice.service.CurrencyAPIService;
import com.api.currencyconverterservice.service.FixerApiService;
import com.api.currencyconverterservice.service.OpenExchangeApiService;

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
    private static final Logger logger = LoggerFactory
    .getLogger(GetCurrenciesController.class);

    private final FixerApiService fixerApiService;
    private final OpenExchangeApiService openExchangeApiService;
    private final CurrencyAPIService currencyAPIService;

    public GetCurrenciesController(FixerApiService fixerApiService, OpenExchangeApiService openExchangeApiService, CurrencyAPIService currencyAPIService){
        this.fixerApiService = fixerApiService;
        this.openExchangeApiService = openExchangeApiService;
        this.currencyAPIService = currencyAPIService;
    }

    @GetMapping("/currencies")
    public ResponseEntity<String> getCurrencies(){
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(fixerApiService.getCurrencies());
        }catch (Exception e){
           logger.error("error occured in fixerAPI service while retrieving currencies: " + e.getMessage()); 
        }

        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(openExchangeApiService.getCurrencies());
        }catch (Exception e){
            logger.error("error occured in openexchangeApi service service while retrieving currencies: " + e.getMessage());
        }

        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(currencyAPIService.getCurrencies());
        }catch(Exception e){
             logger.error("error occured in currencyAPI service while retrieving currencies: " + e.getMessage());
            
        return null;
    
    }

}

}
