package com.api.currencyconverterservice.Exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceFailureException.class)
    public ResponseEntity<ExceptionDTO> ServiceFailureException(){
        ExceptionDTO response = new ExceptionDTO(
         false,
        "External API service is down at the momment, please try again later"
    );
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
        .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> exceptionhandler(){
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
        .body(Map.of("message", "an error has occured, please try again later"));

    }
    
}
