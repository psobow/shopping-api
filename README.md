# 🛒 Shopping API - Monolithic

A **Spring Boot 3 + Java 21** application providing a REST API for a shopping platform.  
It includes user authentication, product management, and order handling.  
Built with **Spring Security, JPA/Hibernate, Docker**, and tested with **JUnit + MockMvc**.

This project follows a **Domain-Driven Design (DDD)** approach:

- Clear separation of **domain**, **services**, and **controllers**
- **Repositories** abstract persistence, keeping domain logic persistence-agnostic
- **DTOs** and **Mappers** define clean boundaries between layers
- **Entities** and **Value Objects** model the core business domain
- Ubiquitous language applied in naming (classes, methods, packages) to reflect business terms

## 🚀 Getting Started

### Prerequisites

- Java 21
- Docker

### Run locally

```bash
# clone repo
git clone https://github.com/psobow/shopping-api.git
cd shopping-api

# build & run tests
./mvnw clean verify

# start DB stack (Postgres on :5432 + Adminer on :8888)
docker compose up -d

# run the app (API on :8080)
./mvnw spring-boot:run
```

## 🛠 Tech Stack

- **Java 21**
- **Spring Boot 3** (Web, Security, Data JPA, Validation)
- **Hibernate / JPA** with PostgreSQL
- **Maven** build system
- **JUnit 5 + Spring Test** for testing
- **Swagger / springdoc-openapi** for API docs
- **Docker / Docker Compose** for containerization

## ✨ Features

### **User authentication**

- Register new users with secure validation
- Login with email + password
- Issue **JWT access** and **refresh tokens**
- Refresh expired access tokens without re-login
- Built-in **role-based access control** with `USER` and `ADMIN` roles:
	- `Public (no auth)` → browse products & categories
	- `USER` → standard shopping actions (profile, cart, orders)
	- `ADMIN` → management actions (user management, product/category admin endpoints)``

### **User profile management**

- Fetch the authenticated user’s profile **with related resources**:
	- Profile payload
	- Current user’s **cart**
	- Current user’s **orders**
- Update profile information
- Delete account
- Ensure all actions are tied to the logged-in user

### **Products & categories**

- Create, read, update, and delete products
- Organize products under categories
- The product listing endpoint supports **dynamic filtering** using Spring Data JPA **Specifications**. Filters
  are optional and composed at runtime (only provided parameters are applied).
- Include image references with product details

### **Image upload**

- Upload and store product images through dedicated endpoints
- Persist images in the database as `@Lob` binary data (BLOBs)
- Maintain images metadata
- Associate multiple images with products via IDs
- Serve images directly via REST endpoints for frontend consumption

### **Cart & orders**

- Add items to the user’s cart
- Update item quantity or remove items
- Clear entire cart
- Checkout flow → generate orders from cart items
- Retrieve order history for authenticated users

### **Unit & integration tests**

- Controller tests with **MockMvc**
- Service layer tests with **Mockito**
- Fixtures for reusable test data
- Covers both happy paths and validation errors

### **Data validation & error handling**

- Request DTOs annotated with `jakarta.validation` constraints (`@NotNull`, `@Size`, `@Email` etc.) and custom
  constraints:`@Distinct`, `@ValidRoles`.
- Automatic validation of request payloads in controllers with `@Valid`
- Centralized exception handling for validation errors → consistent error response format
- Prevents invalid data (e.g., empty product names, negative prices, malformed emails) from entering the system

### **API documentation**

- Auto-generated via **springdoc-openapi**
- Interactive Swagger UI for quick testing
	- Example request body provided in docs
- Descriptive annotations for endpoints and DTOs
- Error responses documented

## 🗃️ Domain Model — Entities

### **User**

- Fields: `id`, `email`, `password`
- Relations: (one-to-one) `UserProfile`, (one-to-many) `UserAuthority`

### **UserProfile**

- Fields: `id`, `firstName`, `lastName`
- Relations:  (one-to-one) `User`, (one-to-one) `UserAddress`, (one-to-one) `Cart`, (one-to-many) `Order`

### **UserAddress**

- Fields: `id`, `cityName`, `streetName`, `streetNumber`, `postCode`
- Relations: (one-to-one) `UserProfile`

### **UserAuthority**

- Fields: `id`, `value`
- Relations: (many-to-one) `User`

### **Category**

- Fields: `id`, `name`
- Relations: (one-to-many) `Product`

### **Product**

- Fields: `id`, `name`, `brandName`, `description`, `price`, `availableQty`
- Relations: (many-to-one) `Category`, (one-to-many) `Image`

### **Image**

- Fields: `id`, `fileName`, `fileType`, `file` (BLOB)
- Relations: (many-to-one) `Product`

### **Cart**

- Fields: `id`
- Relations: (one-to-one) `UserProfile`, (one-to-many) `CartItem`

### **CartItem**

- Fields: `id`, `requestedQty`
- Relations: (many-to-one) `Cart`, (many-to-one) `Product`
- Invariants: quantity ≥ 1; one row per `productId` in a given cart

### **Order**

- Fields: `id`, `orderStatus`, `createdAt`, `totalPrice`
- Relations: (many-to-one) `UserProfile`, (one-to-many) `OrderItem`

### **OrderItem**

- Fields: `id`, `requestedQty`, `productName`, `productBrandName`, `productPrice`, `totalPrice`
- Relations: (many-to-one) `Order`

## 📖 API Overview

The REST API is fully documented with Swagger and docs is available locally after you start the app.

- Swagger UI → [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI spec → [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

![Auth controller](misc/Auth%20controller.png)
![User controller](misc/User%20controller.png)

![Category controller](misc/Category%20controller.png)
![Product controller](misc/Product%20controller.png)
![Image controller](misc/Image%20controller.png)
![Cart controller](misc/Cart%20controller.png)
![Order controller](misc/Order%20controller.png)

![User management controller](misc/User%20management%20controller.png)
![Category management controller](misc/Category%20management%20controller.png)
![Product management controller](misc/Product%20management%20controller.png)
![Image management controller](misc/Image%20management%20controller.png)
![Order management controller](misc/Order%20management%20controller.png)

