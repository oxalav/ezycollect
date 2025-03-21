# Payment application demo

## Prerequisites
- Java 17+
- Maven
- Docker

## Building
To build the executable WAR file:
```
# mvn clean package
```

To build the Docker image:
```
# mvn clean package -Pdocker
```

## Starting
The easiest way to start the application is via `docker-compose`.
First, build the Docker image as described in the previous step, then run:
```
# docker compose up
```
This should start a PostgreSQL instance and a Tomcat container running the demo application.
The REST API will be available at `http://localhost:8080/api/v1/`.

## Testing

Submit payment:
```
# curl --location 'http://localhost:8080/api/v1/payment' \
--header 'Content-Type: application/json' \
--data '{
    "firstName": "Peter",
    "lastName": "Parker",
    "zipCode": "2000",
    "cardNumber": "1234567812345678",
    "amount": 5000
}'
```

Register Web hook:
```
# curl --location 'http://localhost:8080/api/v1/webhook/register' \
--form 'url="http://localhost:8080/unknown"'
```

Unregister Web hook:
```
# curl --location 'http://localhost:8080/api/v1/webhook/unregister' \
--form 'url="http://localhost:8080/unknown"'
```

## RSA key generation
Credit card numbers are encrypted with an RSA private/public key pair.
This approach makes it possible to decouple the payment API from the Web hook notification service.
If these are running as separate applications, the payment only needs to have a public key, which is more secure.

A new RSA key pair can be generated using the following commands: 
```
# openssl genrsa -out private.pem 1024
# openssl rsa -in private.pem -pubout -outform PEM -out public-key.asc
# openssl pkcs8 -topk8 -inform PEM -in private.pem -out private-key.asc -nocrypt
```
Note that the original key needs to be converted to the format recognised by Java Security.
