package com.api.currencyconverterservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.currencyconverterservice.service.ResponseService;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

/**
 * --------------------------------------------------------------
 *  Currency Conversion Controller
 * --------------------------------------------------------------
 * 
 * This controller exposes an endpoint for converting between
 * different currencies using two external APIs (Fixer API and CurrencyAPI).
 * 
 * Example request:
 *   GET /api/v1/convert?from=USD&to=EUR&amount=100
 * 
 * Behaviour:
 *  - Fixer API supports conversion between any currencies.
 *  - CurrencyAPI only supports conversion from USD to any other currency.
 *  - If both APIs return valid results and `from` = USD, 
 *    the average of both results is returned.
 *  - If one API fails, the other still provides a response.
 * 
 */

@RestController
@RequestMapping("/api/v1")
public class CurrencyConversionController {

    private final ResponseService responseService;

    public CurrencyConversionController(ResponseService responseService) {
        this.responseService = responseService;
    }

    /**
     * Converts currency using one or both APIs, depending on availability.
     *
     * @param from   The base currency (e.g., "USD")
     * @param to     The target currency (e.g., "EUR")
     * @param amount The amount to convert
     * @return A Mono emitting the conversion result
     */
    @GetMapping("/convert")
    public Mono<Map<String, Object>> convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {

        return responseService.convertCurrency(from, to, amount);
    }
}
