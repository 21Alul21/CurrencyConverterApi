package com.api.currencyconverterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCaching
@SpringBootApplication
public class CurrencyconverterserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyconverterserviceApplication.class, args);
	}

}
