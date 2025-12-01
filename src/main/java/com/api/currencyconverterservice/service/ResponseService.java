package com.api.currencyconverterservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.currencyconverterservice.entity.RecentRateEntity;
import com.api.currencyconverterservice.repository.RecentRateRepository;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseService {

    private static final Logger logger = LoggerFactory.getLogger(ResponseService.class);
    private final CurrencyAPIService currencyAPIService;
    private final FixerApiService fixerApiService;
    private final OpenExchangeApiService openExchangeApiService;
    private final RecentRateRepository recentRateRepository;

    public ResponseService(CurrencyAPIService currencyAPIService, FixerApiService fixerApiService, 
        OpenExchangeApiService openExchangeApiService, RecentRateRepository recentRateRepository) {
        this.currencyAPIService = currencyAPIService;
        this.fixerApiService = fixerApiService;
        this.openExchangeApiService = openExchangeApiService;
        this.recentRateRepository = recentRateRepository;
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
                    response.put("message", "An unexpected error occurred");
                    return Mono.just(response);
                });
    }


    public Mono<Object> getRates(String base){
        Mono<String> fixerResponse = fixerApiService.getRates(base);
        Mono<String> openExchangeResponse = openExchangeApiService.getRates(base);

       return Mono.zip(fixerResponse, openExchangeResponse)
        .map(tuple -> {
            String fixerTupple = tuple.getT1();
            String openExchangeTupple = tuple.getT2();

             // creating a new db instance
             RecentRateEntity recentRate = new RecentRateEntity();
                
            if (!fixerTupple.isEmpty()) {

                // persisting request info to db
                recentRate.setRequestTime(LocalDateTime.now());
                 recentRate.setBaseSymbol(base);
                 recentRate.setStatus(true);

                  // persisting data in a non-blocking way
                  Mono.fromRunnable(() -> recentRateRepository
                  .save(recentRate))
                  .subscribe();
                    
                return fixerTupple;
            } else if (!openExchangeTupple.isEmpty()) {
                
                  recentRate.setRequestTime(LocalDateTime.now());
                  recentRate.setBaseSymbol(base);
                  recentRate.setStatus(true);

                // persisting data in a non-blocking way
                  Mono.fromRunnable(() -> recentRateRepository
                  .save(recentRate))
                  .subscribe();
                    return openExchangeTupple;
            } else {
                HashMap<String, Object> hm = new HashMap<>();
                hm.put("message", "Service not available, please try again later");

                recentRate.setRequestTime(LocalDateTime.now());
                 recentRate.setBaseSymbol(base);
                 recentRate.setStatus(false);      
                 Mono.fromRunnable(() -> recentRateRepository.save(recentRate))
                 .subscribe();
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(hm);
            }
        })
        .onErrorResume(e -> {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("message", "Unexpected error: " + e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hm));
        });


    }
}
