# Banking — Customer Service

`customer-service` owns **customer identity** in the Banking microservices system. It stores and
manages a customer's `customerId`, `name`, `email`, and `phoneNumber`, backed by **MongoDB**.

- **Group:** `com.banking`
- **Artifact:** `customer-service`
- **Base package:** `com.banking.customer`
- **Port:** `8081`
- **Spring Boot:** 4.1.0
- **Java:** 21
- **Database:** MongoDB (`customer_service_db`)

---

## Architecture

The Banking system is a **multi-repository** microservices platform. Each service is deployed
independently and registers with Eureka; clients reach services only through the API Gateway.

```
                 ┌─────────────┐
   Client  ───▶  │ api-gateway │
                 └──────┬──────┘
                        │  (service discovery via Eureka)
   ┌────────────────────┼─────────────────────────────┐
   ▼            ▼        ▼            ▼                 ▼
customer-   account-  wallet-   transaction-       audit-
 service    service   service     service          service
   │
   ▼
MongoDB (customer_service_db)
```

- **Service discovery:** Eureka (`@EnableDiscoveryClient`)
- **Inter-service calls:** OpenFeign (`@EnableFeignClients`)
- **Observability:** Spring Boot Actuator
- **API docs:** springdoc OpenAPI / Swagger UI

### Package structure

```
com.banking.customer
├── CustomerServiceApplication
├── controller        → CustomerController
├── service           → CustomerService
│   └── impl          → CustomerServiceImpl
├── repository        → CustomerRepository
├── entity            → Customer
├── dto
│   ├── request       → CustomerRequestDTO
│   └── response      → CustomerResponseDTO, ErrorResponseDTO
├── exception         → DuplicateCustomerException, InvalidEmailException,
│                        InvalidPhoneNumberException, CustomerNotFoundException
├── advice            → GlobalExceptionHandler
└── config            → SwaggerConfig, MongoConfig
```

> **Config note (Spring Boot 4.1):** the Mongo connection is defined explicitly in
> `config/MongoConfig` (extending `AbstractMongoClientConfiguration`). It reads
> `spring.data.mongodb.host` / `port` / `database` from `application.yml`, pins the database
> to **`customer_service_db`**, sets the entity mapping base package to `com.banking.customer.entity`,
> and enables automatic index creation so the unique index on `customerId` is created at
> startup. This is required because, in the Boot 4.1 Mongo auto-configuration, relying on the
> URI/`database` property alone caused writes to fall back to the default `test` database.

---

## Business rules

1. `customerId` must be unique.
2. `name` cannot be blank.
3. `email` must be a valid email address.
4. `phoneNumber` must contain exactly 10 digits.

---

## Setup

### Prerequisites

- JDK 21
- Maven 3.9+
- MongoDB running locally on `mongodb://localhost:27017`
- (Optional) Eureka server running on `http://localhost:8761/eureka/`

### Start MongoDB

**Docker (recommended):**

```bash
docker run -d --name banking-mongo -p 27017:27017 mongo:7
```

**macOS (Homebrew):**

```bash
brew services start mongodb-community
```

The database `customer_service_db` and the `customers` collection are created automatically on first write.
The unique index on `customerId` is created automatically (`spring.data.mongodb.auto-index-creation=true`).

---

## Run

```bash
mvn clean spring-boot:run
```

or build and run the jar:

```bash
mvn clean package
java -jar target/customer-service-0.0.1-SNAPSHOT.jar
```

The service starts on **http://localhost:8081**.

---

## URLs

| Purpose            | URL                                            |
|--------------------|------------------------------------------------|
| Swagger UI         | http://localhost:8081/swagger-ui.html          |
| OpenAPI JSON       | http://localhost:8081/v3/api-docs              |
| Actuator health    | http://localhost:8081/actuator/health          |

> This service uses **MongoDB**, not H2 — there is no H2 console. To inspect data use
> **MongoDB Compass** (`mongodb://localhost:27017`) or `mongosh`:
>
> ```bash
> mongosh "mongodb://localhost:27017/customer_service_db" --eval "db.customers.find().pretty()"
> ```

---

## API Documentation

Base path: `/customers`

### 1. Create customer

`POST /customers`

**Request body**

```json
{
  "customerId": "CUST1001",
  "name": "Asha Rao",
  "email": "asha@example.com",
  "phoneNumber": "9876543210"
}
```

**Response — `201 Created`**

```json
{
  "customerId": "CUST1001",
  "name": "Asha Rao",
  "email": "asha@example.com",
  "phoneNumber": "9876543210"
}
```

### 2. Get customer by customerId

`GET /customers/{customerId}`

**Response — `200 OK`**

```json
{
  "customerId": "CUST1001",
  "name": "Asha Rao",
  "email": "asha@example.com",
  "phoneNumber": "9876543210"
}
```

### Validation rules

| Field         | Rules                                          |
|---------------|------------------------------------------------|
| `customerId`  | `@NotBlank`                                    |
| `name`        | `@NotBlank`                                    |
| `email`       | `@NotBlank`, `@Email`                          |
| `phoneNumber` | `@NotBlank`, `@Pattern(^[0-9]{10}$)`           |

### Standard error response

Every failed request returns the same shape:

```json
{
  "timestamp": "2026-07-16T10:15:30",
  "status": 400,
  "error": "InvalidEmailException",
  "message": "Email is invalid",
  "path": "/customers"
}
```

### HTTP status mapping

| Exception                      | HTTP status               |
|--------------------------------|---------------------------|
| `InvalidEmailException`        | `400 BAD REQUEST`         |
| `InvalidPhoneNumberException`  | `400 BAD REQUEST`         |
| Bean validation failure        | `400 BAD REQUEST`         |
| `CustomerNotFoundException`    | `404 NOT FOUND`           |
| `DuplicateCustomerException`   | `409 CONFLICT`            |
| Any unhandled exception        | `500 INTERNAL SERVER ERROR` |

---

## Sample requests (curl)

**Create a customer**

```bash
curl -i -X POST http://localhost:8081/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST1001",
    "name": "Asha Rao",
    "email": "asha@example.com",
    "phoneNumber": "9876543210"
  }'
```

**Get a customer**

```bash
curl -i http://localhost:8081/customers/CUST1001
```

**Trigger a 400 (invalid phone)**

```bash
curl -i -X POST http://localhost:8081/customers \
  -H "Content-Type: application/json" \
  -d '{"customerId":"CUST1002","name":"Bad Phone","email":"bad@example.com","phoneNumber":"12345"}'
```

**Trigger a 409 (duplicate customerId)** — run the create request twice.

**Trigger a 404 (not found)**

```bash
curl -i http://localhost:8081/customers/CUST9999
```

---

## Postman

Import [`postman/customer-service.postman_collection.json`](postman/customer-service.postman_collection.json)
into Postman. It ships with a `baseUrl` variable (`http://localhost:8081`) and requests covering the
`201`, `200`, `400`, `409`, and `404` scenarios.

---

## Testing

```bash
mvn test
```

- `CustomerServiceTest` — JUnit 5 + Mockito unit tests for the service layer.
- `CustomerControllerTest` — `@WebMvcTest` + MockMvc slice tests for the controller and error mapping.

---

## Maven commands

```bash
mvn clean            # remove target/
mvn compile          # compile sources
mvn test             # run unit tests
mvn package          # build the runnable jar
mvn spring-boot:run  # run the service
```

---

## Git commands

```bash
git init
git add .
git commit -m "Initial commit: customer-service"
git branch -M main
git remote add origin <banking-customer-service-repo-url>
git push -u origin main
```
