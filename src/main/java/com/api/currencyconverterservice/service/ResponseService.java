package com.api.currencyconverterservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.api.currencyconverterservice.service.CurrencyAPIService;
import com.api.currencyconverterservice.service.FixerApiService;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseService {

    private static final Logger logger = LoggerFactory.getLogger(ResponseService.class);

    private final CurrencyAPIService currencyAPIService;
    private final FixerApiService fixerApiService;

    public ResponseService(CurrencyAPIService currencyAPIService, FixerApiService fixerApiService) {
        this.currencyAPIService = currencyAPIService;
        this.fixerApiService = fixerApiService;
    }

    public Mono<Map<String, Object>> convertCurrency(String from, String to, BigDecimal amount) {
        Map<String, Object> response = new HashMap<>();

        // Call Fixer API
        Mono<JsonNode> fixerResponse = fixerApiService.convertCurrency(from, to, amount)
                .onErrorResume(e -> {
                    logger.error("Fixer API error: {}", e.getMessage());
                    return Mono.just((JsonNode) null);
                });

        // Call CurrencyAPI
        Mono<JsonNode> currencyAPIResponse = currencyAPIService.convertCurrency(amount, to)
                .onErrorResume(e -> {
                    logger.error("CurrencyAPI error: {}", e.getMessage());
                    return Mono.just((JsonNode) null);
                });

        // Combine both results
        return Mono.zip(fixerResponse, currencyAPIResponse)
                .map(tuple -> {
                    JsonNode fixerNode = tuple.getT1();
                    JsonNode currencyAPINode = tuple.getT2();

                    BigDecimal fixerConvertedValue = null;
                    if (fixerNode != null && fixerNode.has("result")) {
                        fixerConvertedValue = new BigDecimal(fixerNode.get("result").asText());
                    }

                    BigDecimal currencyAPIConvertedValue = null;
                    if (currencyAPINode != null && currencyAPINode.has("data") && currencyAPINode.get("data").has(to)) {
                        currencyAPIConvertedValue = new BigDecimal(currencyAPINode.get("data").get(to).asText());
                    }

                    if (fixerConvertedValue != null && currencyAPIConvertedValue != null && from.equals("USD")) {
                        BigDecimal averageConvertedValue = fixerConvertedValue.add(currencyAPIConvertedValue)
                                .divide(BigDecimal.valueOf(2));
                        response.put("success", true);
                        response.put("message", "Currency conversion fetched from both APIs, and average returned.");
                        response.put("convertedValue", averageConvertedValue);
                    } else if (fixerConvertedValue != null) {
                        response.put("success", true);
                        response.put("message", "Conversion fetched from Fixer API.");
                        response.put("convertedValue", fixerConvertedValue);
                    } else if (currencyAPIConvertedValue != null && from.equals("USD")) {
                        response.put("success", true);
                        response.put("message", "Conversion fetched from CurrencyAPI.");
                        response.put("convertedValue", currencyAPIConvertedValue);
                    } else {
                        response.put("success", false);
                        response.put("message", "Failed to fetch conversion data from both APIs.");
                    }

                    response.put("from", from);
                    response.put("to", to);
                    response.put("amount", amount);

                    return response;
                })
                .onErrorResume(e -> {
                    logger.error("Unexpected error: {}", e.getMessage());
                    response.put("success", false);
                    response.put("message", "An unexpected error occurred: " + e.getMessage());
                    return Mono.just(response);
                });
    }
}
