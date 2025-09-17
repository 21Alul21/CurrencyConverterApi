package com.api.currencyconverterservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.currencyconverterservice.service.FixerApiService;

@RestController
@RequestMapping("api/v1")
public class GetCurrenciesController {
    private final FixerApiService fixerApiService;

    public GetCurrenciesController(FixerApiService fixerApiService){
        this.fixerApiService = fixerApiService;
    }

    @GetMapping("/currencies")
    public String getCurrencies(){
        return fixerApiService.getCurrencies();
    }

}
