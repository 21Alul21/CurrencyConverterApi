package com.api.currencyconverterservice.Exception;


public class ServiceFailureException extends Exception {
    public ServiceFailureException(String message){
        super(message);
    }
    
    public ServiceFailureException(String message, Throwable cause){
        super(message);
    }


}
