// package com.api.currencyconverterservice.controller;

// import java.time.LocalDateTime;
// import java.util.HashMap;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.api.currencyconverterservice.entity.RecentRateEntity;
// import com.api.currencyconverterservice.repository.RecentRateRepository;
// import com.api.currencyconverterservice.service.FixerApiService;
// import com.api.currencyconverterservice.service.OpenExchangeApiService;

// import reactor.core.publisher.Mono;

// /*
//  * - This is the RatesController module.
//  * - The RatesController saves the request in the DB and returns the current exchange rates of
//  * different supported currencies.
//  * 
//  * - A base currency symbol has to be passed as request parameter
//  * 
//  * --------------------------------------------------------------
//  *      send GET request to api/v1/rates?base=USD
//  * --------------------------------------------------------------
//  * 
//  * - There is primary and secondary external APIs for a resilient
//  * fallback mechanism.
//  * 
//  * - If both APIs fail, cached results is returned with a warning..
//  * 
//  */

// @RestController
// @RequestMapping("api/v1")
// public class RatesController {

//     private final FixerApiService fixerApiService;
//     private final OpenExchangeApiService openExchangeApiService;
//     private final RecentRateRepository recentRateRepository;

//     public RatesController(FixerApiService fixerApiService, OpenExchangeApiService 
//     openExchangeApiService, RecentRateRepository recentRateRepository){
//         this.fixerApiService = fixerApiService;
//         this.openExchangeApiService = openExchangeApiService;
//         this.recentRateRepository = recentRateRepository;

//     }


//     @GetMapping("/rates")
//     public Mono<Object> getRates(@RequestParam String base){

//         Mono<String> fixerResponse =  fixerApiService.getRates(base);
//         Mono<String> openExchangeResponse = openExchangeApiService.getRates(base);
        

//         return Mono.zip(fixerResponse.defaultIfEmpty(null), openExchangeResponse
//         .defaultIfEmpty(null))
//         .map(tuple -> {
//             String fixerTupple = tuple.getT1();
//             String openExchangeTupple = tuple.getT2();

//             RecentRateEntity recentRate = new RecentRateEntity();

//             if (fixerTupple != null){
//                 recentRate.setRequestTime(LocalDateTime.now());
//                 recentRate.setBaseSymbol(base);
//                 recentRate.setStatus(true);      
//                 recentRateRepository.save(recentRate);
//                 return fixerTupple;
//             }

//             else if(openExchangeTupple != null){
//                 recentRate.setRequestTime(LocalDateTime.now());
//                 recentRate.setBaseSymbol(base);
//                 recentRate.setStatus(true);      
//                 recentRateRepository.save(recentRate);
//                 return openExchangeTupple;
//             }

//             else{
//                 recentRate.setRequestTime(LocalDateTime.now());
//                 recentRate.setBaseSymbol(base);
//                 recentRate.setStatus(false);      
//                 recentRateRepository.save(recentRate);

//         } 
//         })
//        .onErrorResume(e -> {
//             HashMap<String, Object> hm = new HashMap<>();
//             hm.put("message", "Unexpected error: " + e.getMessage());
//             return Mono.just(hm);


//     }
// }







//////////////////////////////////////////////////////////////////
package com.api.currencyconverterservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.currencyconverterservice.entity.RecentRateEntity;
import com.api.currencyconverterservice.repository.RecentRateRepository;
import com.api.currencyconverterservice.service.FixerApiService;
import com.api.currencyconverterservice.service.OpenExchangeApiService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1")
public class RatesController {

    private final FixerApiService fixerApiService;
    private final OpenExchangeApiService openExchangeApiService;
    private final RecentRateRepository recentRateRepository;

    public RatesController(FixerApiService fixerApiService, OpenExchangeApiService openExchangeApiService, RecentRateRepository recentRateRepository) {
        this.fixerApiService = fixerApiService;
        this.openExchangeApiService = openExchangeApiService;
        this.recentRateRepository = recentRateRepository;
    }

    @GetMapping("/rates")
public Mono<Object> getRates(@RequestParam String base) {
    Mono<String> fixerResponse = fixerApiService.getRates(base)
        .onErrorResume(e -> Mono.just(""));
    Mono<String> openExchangeResponse = openExchangeApiService.getRates(base)
        .onErrorResume(e -> Mono.just(""));

    return Mono.zip(fixerResponse, openExchangeResponse)
        .map(tuple -> {
            String fixerTupple = tuple.getT1();
            String openExchangeTupple = tuple.getT2();

            if (!fixerTupple.isEmpty()) {
                return fixerTupple;
            } else if (!openExchangeTupple.isEmpty()) {
                return openExchangeTupple;
            } else {
                HashMap<String, Object> hm = new HashMap<>();
                hm.put("message", "Service not available, please try again later");
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






