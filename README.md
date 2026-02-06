# Expense Tracker API

Expense Tracker API built with **Spring Boot**, **Java 21**, **PostgreSQL** and **Docker**.

## Requirements

### Local execution
- Java 21
- Maven 3.9+
- PostgreSQL

### Docker execution
- Docker
- Docker Compose

---

## Environment variables

Create a `.env` file based on `.env.example`.

```bash
cp .env.example .env
```
---

## Running locally (without Docker)
> Local execution is supported, but Docker is the recommended approach for consistency.

1. Start PostgreSQL locally
2. Configure the `.env` file with local database credentials
3. Export environment variables
4. Run the application:

```bash
mvn spring-boot:run
```
The API will be available at:

```bash
http://localhost:8080
```

---

## Running with Docker (recommended)
### Build and start containers
```bash
docker compose up --build
```

This will:
- Build the API image
- Start PostgreSQL
- Run the application with the prod profile

The API will be available at:
```bash
http://localhost:8080
```

### Stop containers
```bash
docker compose down
```

### Health check
```bash
GET /actuator/health
```

## API Documentation (Swagger)
```bash
http://localhost:8080/swagger-ui.html
```
