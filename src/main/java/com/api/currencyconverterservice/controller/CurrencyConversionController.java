package com.api.currencyconverterservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.currencyconverterservice.service.CurrencyAPIService;
import com.api.currencyconverterservice.service.FixerApiService;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.HashMap;

/*
 * - This module holds the CurrencyConversionController.
 * It exposes the endpoint for converting between 
 * different currencies using different external APIs (primary and secondary).
 * 
 * ---------------------------------------------------------------------- 
 *      send GET request to api/v1/convert?from=USD&to=EUR&amount=100
 * ----------------------------------------------------------------------
 * 
 * - The external fixer API service allows you to freely convert 
 * from any currency to another,
 * but the external currency API sevice only allows you to convert
 * from USD to any other available currency of your choice,
 * this implies that the secondary API service (Currency API) is 
 * only triggered if the conversion is from USD. 
 * 
 * - If the from request parameter is set to USD, the values of
 * both APIs are aggregated and the average returned.
 *  
 */


@RestController
@RequestMapping("api/v1")
public class CurrencyConversionController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);
    private final FixerApiService fixerApiService;
    private final CurrencyAPIService currencyAPIService;

    public CurrencyConversionController(FixerApiService fixerApiService, 
    CurrencyAPIService currencyAPIService) {
        this.fixerApiService = fixerApiService;
        this.currencyAPIService = currencyAPIService;
    }


    @GetMapping("/convert")
    public Mono<HashMap<String, Object>> convertCurrency(@RequestParam String from,
                                                    @RequestParam String to,
                                                    @RequestParam BigDecimal amount) {
                                                        
    HashMap<String, Object> response = new HashMap<>();

    Mono<JsonNode> fixerResponse = fixerApiService.convertCurrency(from, to, amount)
    .onErrorResume(e -> {
        logger.error("Fixer API error: " + e.getMessage());
        return Mono.just((JsonNode) null);
    });
    Mono<JsonNode> currencyAPIResponse = currencyAPIService.convertCurrency(amount, to)
    .onErrorResume(e -> {
        logger.error("CurrencyAPI error: " + e.getMessage());
        return Mono.just((JsonNode) null);
    });
            
    return Mono.zip(fixerResponse, currencyAPIResponse)
            .map(tuple -> {
                JsonNode fixerNode = tuple.getT1();
                JsonNode currencyAPINode = tuple.getT2();

                // Fixer API allows conversion from any currency to another
                BigDecimal fixerConvertedValue = null;
                if (fixerNode != null && fixerNode.has("result")) {
                    fixerConvertedValue = new BigDecimal(fixerNode.get("result").asText());
                }

                // CurrencyAPI only allows conversion from USD to other currencies
                BigDecimal currencyAPIConvertedValue = null;
                if (currencyAPINode != null && currencyAPINode.has("data") && currencyAPINode.get("data").has(to)) {
                    currencyAPIConvertedValue = new BigDecimal(currencyAPINode.get("data").get(to).asText());
                }

                // return average if both APIs return values and from is USD
                if (fixerConvertedValue != null && currencyAPIConvertedValue != null && from.equals("USD")) {
                    BigDecimal averageConvertedValue = fixerConvertedValue.add(currencyAPIConvertedValue)
                            .divide(BigDecimal.valueOf(2));
                    response.put("success", true);
                    response.put("message", "Currency conversion fetched from both APIs, and average returned");
                    response.put("converted value", averageConvertedValue);
                    response.put("from", from);
                    response.put("to", to);
                } else if (fixerConvertedValue != null) {
                    response.put("success", true);
                    response.put("converted value", fixerConvertedValue);
                    response.put("from", from);
                    response.put("to", to);
                } else if (currencyAPIConvertedValue != null && from.equals("USD")) {
                    response.put("success", true);
                    response.put("converted value", currencyAPIConvertedValue);
                    response.put("from", from);
                    response.put("to", to);
                } else {
                    response.put("success", false);
                    response.put("message", "Failed to fetch conversion data from both APIs.");
                }
                return response;
            })
            .onErrorResume(e -> {
                response.put("success", false);
                response.put("message", "An error occurred");
                return Mono.just(response);
            });
    }
}