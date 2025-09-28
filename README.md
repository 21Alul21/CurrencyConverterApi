# CurrencyConverterApi
This project titled CurrencyConverterApi is a backend project 
built with Java and Spring Boot framework.

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
- Getting a list of supposted currencies (GET api/v1/currencies)
- Rates history within the last 24 hours (GET api/v1/health)

## Features
- Caching (using redis)
- Containerization (Docker)
- Database for storing recent rate requests


## Status
Ongoing
