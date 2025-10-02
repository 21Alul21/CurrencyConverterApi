package com.api.currencyconverterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class CurrencyconverterserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyconverterserviceApplication.class, args);
	}

}
