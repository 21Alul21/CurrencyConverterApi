package com.api.currencyconverterservice.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.api.currencyconverterservice.controller.GetCurrenciesController;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Mono;

@Service
public class GetCurrencyCircuitBreakerService {

    private static final Logger logger = LoggerFactory
    .getLogger(GetCurrenciesController.class);

    private final FixerApiService fixerApiService;
    private final OpenExchangeApiService openExchangeApiService;
    private final CurrencyAPIService currencyAPIService;

    public GetCurrencyCircuitBreakerService(FixerApiService fixerApiService, OpenExchangeApiService openExchangeApiService,CurrencyAPIService currencyAPIService){
        this.fixerApiService = fixerApiService;
        this.openExchangeApiService = openExchangeApiService;
        this.currencyAPIService = currencyAPIService;
    }

    @CircuitBreaker(name = "fixerAPIGetCurrencies", fallbackMethod = "openExchangeAPIGetCurrencies")
    public Mono<String> fixerAPIGetCurrencies(){
        logger.info("now in the fixerAPIGetCurrencies service method." );
        return fixerApiService.getCurrencies();
    }

     @CircuitBreaker(name = "openExchangeAPIGetCurrencies", fallbackMethod = "currencyAPIGetCurrencies")
    public String openExchangeAPIGetCurrencies(Throwable cause){
        logger.info("fixerAPIGetcurrencies service have failed now in the openExchangeAPIGetCurrencies service method. Failure cause: " + cause );
        return openExchangeApiService.getCurrencies();
    }

    @CircuitBreaker(name = "currencyAPIGetCurrencies", fallbackMethod = "errorResponse")
    public String currencyAPIGetCurrencies(Throwable cause){
        logger.info("openexchangeAPI service have failed, now in the currencyAPIGetCurrencies service method. Failure cause: " + cause);
        return currencyAPIService.getCurrencies();
    }

    public Map<String, Object> errorResponse(Throwable cause) throws ServiceException{
        throw new ServiceException("service currently unavailable, please try again later.. Failure cause: " + cause);
    }

    
}
