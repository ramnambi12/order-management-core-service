# Order Management Core Service

A RESTful Order Management Core Service built with Spring Boot. The application manages customers, products, orders, inventory deduction, and customer order KPIs.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Flyway Database Migration
- Bean Validation
- SpringDoc OpenAPI / Swagger
- JUnit 5 / Mockito
- Docker / Docker Compose
- Spring Boot Actuator
- Maven

## Architecture

The application follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

The project is organized by feature and responsibility:

```text
controller
dto
entity
repository
service
exception
enums
```

## Features

### Customer Management

- Create customer
- Update customer
- Fetch customer by ID
- Fetch all customers
- Unique email validation
- Unique phone validation

### Product Management

- Create product
- Update product
- Fetch product by ID
- Fetch all products
- Product price validation
- Stock validation

### Order Management

- Create orders
- Support multiple products in an order
- Validate customer existence
- Validate product existence
- Validate sufficient stock
- Automatically deduct stock after successful order placement
- Calculate item totals
- Calculate total order amount
- Fetch order by ID
- Fetch all orders
- Fetch orders by customer ID
- Transactional order processing

### Reporting / KPIs

- Total orders placed by each customer
- Top 5 customers by number of orders

## Database

PostgreSQL is used as the primary database.

Flyway manages database schema migrations:

```text
V1__initial_schema.sql
V2__add_indexes.sql
```

Database indexes are added for commonly queried relationships such as:

- `orders.customer_id`
- `order_items.order_id`
- `order_items.product_id`

Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This ensures Hibernate validates the database schema instead of modifying it automatically.

## API Documentation

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

## Health Check

Spring Boot Actuator provides a health endpoint:

```text
GET /actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

## Running Locally

### Prerequisites

- Java 17
- Maven
- Docker Desktop

### Start PostgreSQL and Application

```bash
docker compose up --build
```

The application will be available at:

```text
http://localhost:8080
```

### Stop the Application

```bash
docker compose down
```

## API Endpoints

### Customers

```text
POST   /api/v1/customers
PUT    /api/v1/customers/{id}
GET    /api/v1/customers/{id}
GET    /api/v1/customers
```

### Products

```text
POST   /api/v1/products
PUT    /api/v1/products/{id}
GET    /api/v1/products/{id}
GET    /api/v1/products
```

### Orders

```text
POST   /api/v1/orders
GET    /api/v1/orders/{id}
GET    /api/v1/orders
GET    /api/v1/orders/customer/{customerId}
```

### KPIs

```text
GET    /api/v1/kpis/top-customers
GET    /api/v1/kpis/customer-order-summary
```

## Testing

Unit tests are implemented using JUnit 5 and Mockito.

The project currently contains tests covering:

- Customer service
- Product service
- Order service
- KPI service
- Validation and exception scenarios

## Business Transaction Example

When an order is placed:

```text
Customer validation
        ↓
Product validation
        ↓
Stock validation
        ↓
Calculate item total
        ↓
Deduct product stock
        ↓
Calculate order total
        ↓
Save order
```

Order processing is transactional, so a failed order does not leave partial inventory changes.

## Docker Architecture

```text
                    Docker Compose
                         │
              ┌──────────┴──────────┐
              │                     │
              ▼                     ▼
      Spring Boot App          PostgreSQL
       Port 8080                Port 5432
              │                     │
              └──── Docker Network ┘
```

The Spring Boot application connects to PostgreSQL using the Docker Compose service name:

```text
postgres:5432
```

## Project Status

The core Order Management functionality is implemented and tested.

- Customer Management ✅
- Product Management ✅
- Order Management ✅
- Inventory Management ✅
- KPI Reporting ✅
- Exception Handling ✅
- Database Migrations ✅
- Database Indexes ✅
- Unit Tests ✅
- Swagger/OpenAPI ✅
- Actuator Health Check ✅
- Dockerization ✅
- End-to-End Docker Testing ✅