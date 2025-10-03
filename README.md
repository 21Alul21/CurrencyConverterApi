# CurrencyConverterAPI
This project titled CurrencyConverterApi is a backend project 
built with Java and Spring Boot framework.

![Picture showng openAPI UI interface of the the routes](https://github.com/21Alul21/CurrencyConverterApi/blob/main/Screenshot%202025-10-03%20192917.png)

## Project Scope
The project involes integrating multiple
third party APIs for aggregating and returning data
with little chance for failure—this is achieved by
implementing a failover mechanism that switches to 
another external API provider when the primary provider 
is down.

The project provide endpoints for:
- Realtime conversion from one currency to another (GET api/v1/convert?
from=USD&to=EUR&amount=100)
- Current rates of all available currencies (GET api/v1/rates?base=USD)
- Getting a list of supported currencies (GET api/v1/currencies)
- Rates history within the last 24 hours (GET api/v1/health)

## Features
- Caching (simple in-memory caching)
- Containerization (Docker)
- Database for storing recent rate requests


## Status
Ongoing
