package com.api.currencyconverterservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class RateHistoryCircuitBreakerService {
    private static final Logger logger =  LoggerFactory
    .getLogger(RateHistoryCircuitBreakerService.class);

    private final FixerApiService fixerApiService;
    private final CurrencyAPIService currencyAPIService;
    
    public RateHistoryCircuitBreakerService(FixerApiService fixerApiService, CurrencyAPIService currencyAPIService){
       this.fixerApiService = fixerApiService;
       this.currencyAPIService = currencyAPIService;
    }

    @CircuitBreaker(name = "fixerRateHistory", fallbackMethod = "currencyRateHistory")
    public String fixerRateHistory(){
        logger.info("Inside the fixerRate History method");
        return fixerApiService.rateHistory();
    }

    @CircuitBreaker(name = "currencyRateHistory", fallbackMethod = "erroResponse")
    public String currencyRateHistory(){
        logger.info(" fixerRateHistory service has failed... currently Inside the fixerRate History method");
        return currencyAPIService.rateHistory();
    }

    public Map<String, Object> errorResponse() throws ServiceException{
        throw new ServiceException("both external API services have failed, please try again later");

    }

}
