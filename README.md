# Event Driven Architecture

This project has 4 modules:
 - base-domains: contains Data Transfer Object (DTO) common to all services.
 - email-service: pretends to send an email. It consumes a Kafka message, then publish a message to an 
ActiveMQ queue from which it later consumes that message and logs it (pretending to send and email).
 - order-service: exposes an endpoint (localhost:8181/api/v1/orders) with a POST method to be able to 
publish messages to Kafka.
 - stock-service: saves orders to a PostgreSQL database, after consuming a Kafka message.


## Run

Docker Desktop must be running. Then in a console execute: `docker-compose up -d`

## Curl
To hit the endpoint mentioned above, **order-service** must be up and running. Then you can execute:

`curl -i -d '{"name": "Laptop", "quantity": 1, "price": 999.99}' 
-H "Content-Type: application/json" -X POST http://localhost:8181/api/v1/orders`