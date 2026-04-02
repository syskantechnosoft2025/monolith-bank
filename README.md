# Monolith Bank

Spring Boot 4.0.5 + Java 25 monolithic online banking backend with JWT security, roles, accounts, transactions, and a minimal React front-end.

## Features implemented

- Authentication with JWT access + refresh tokens
- Role-based access (ROLE_CUSTOMER, ROLE_MANAGER, ROLE_ADMIN)
- Account management (deposit, withdraw, transfer)
- Transaction history and status tracking
- Excel/PDF report exports (Apache POI + iText 7)
- Spring profiles: dev/test/stage/prod
- DB persistence: H2 for dev/test, MySQL for stage/prod
- Seed data in `src/main/resources/data.sql`
- Unit tests for services using JUnit 5 + Mockito
- React frontend with login, register, role-based dashboards (customer, manager, admin), deposit/withdraw/transfer forms, payee management, transaction history

## Required runtime

- Java 25
- Maven 3.9+
- MySQL 8.x (8.0.43 suggested, `com.mysql:mysql-connector-j:8.0.33` is used due artifact availability)

## Backend Run

1. Configure `application-stage.properties` (or `application-prod.properties`) with your MySQL credentials.
2. Create database:

```powershell
mysql -u root -p
CREATE DATABASE monolith_bank;
```

3. Build and run:

```powershell
cd c:\Users\2404030\Downloads\CodeWS\monolith-bank
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=stage
```

## Frontend Run

```powershell
cd c:\Users\2404030\Downloads\CodeWS\monolith-bank\frontend
npm install
npm start
```

## API endpoints

- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/accounts/me`
- `POST /api/transactions/deposit`
- `POST /api/transactions/withdraw`
- `POST /api/transactions/transfer`
- `GET /api/transactions/search`

## Testing with cURL

Base URL: `http://localhost:8080`

### 1. Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Password123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 2. Login and Get Tokens

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Password123!"
  }'
```

**Response includes:**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "expiresIn": 3600
}
```

### 3. Get Current User Accounts

```bash
curl -X GET http://localhost:8080/api/accounts/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 4. Deposit Money

```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "accountId": 1,
    "amount": 500.00,
    "description": "Initial deposit"
  }'
```

### 5. Withdraw Money

```bash
curl -X POST http://localhost:8080/api/transactions/withdraw \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "accountId": 1,
    "amount": 100.00,
    "description": "ATM withdrawal"
  }'
```

### 6. Transfer Funds Between Accounts

```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "fromAccountId": 1,
    "toAccountId": 2,
    "amount": 250.00,
    "description": "Payment to friend"
  }'
```

### 7. Search Transaction History

```bash
curl -X GET "http://localhost:8080/api/transactions/search?accountId=1&status=COMPLETED&limit=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 8. Refresh Access Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### 9. Create Savings Account

```bash
curl -X POST http://localhost:8080/api/accounts/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "accountType": "SAVINGS",
    "initialBalance": 1000.00
  }'
```

**Response:**
```json
{
  "id": 2,
  "accountNumber": "SAV123456789",
  "type": "SAVINGS",
  "balance": 1000.00,
  "createdAt": "2026-04-02T10:30:00Z"
}
```

### 10. Create Current Account

```bash
curl -X POST http://localhost:8080/api/accounts/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "accountType": "CURRENT",
    "initialBalance": 5000.00
  }'
```

### 11. Create Deposit Account

```bash
curl -X POST http://localhost:8080/api/accounts/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "accountType": "DEPOSIT",
    "initialBalance": 10000.00
  }'
```

### 12. Create Loan Account

```bash
curl -X POST http://localhost:8080/api/accounts/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "accountType": "LOAN",
    "initialBalance": 0.00
  }'
```

### 13. Access H2 Console (Dev Profile Only)

```bash
# In browser: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:bankdb
# Username: sa
# Password: (leave blank)
```

### 14. Access Swagger UI (API Documentation)

```bash
# In browser: http://localhost:8080/swagger-ui.html
```

### 15. Access OpenAPI Specification

```bash
# In browser or curl: http://localhost:8080/v3/api-docs
# Returns complete OpenAPI 3.0 specification in JSON format
```

## Notes

- `src/main/resources/application.properties` defaults to H2. Change `spring.profiles.active` for production.
- Use `curl` or Postman to test with bearer token.
- Refresh token endpoint via `/api/auth/refresh` implemented in `AuthController`.
- For Windows PowerShell, escape the `@` symbol in JSON with backticks: `` `@ ``
- Replace `YOUR_ACCESS_TOKEN` and `YOUR_REFRESH_TOKEN` with actual tokens from login response.
- H2 console is only available in dev profile (`-Dspring-boot.run.profiles=dev`).
- CORS is configured to allow requests from `http://localhost:3000` (React frontend).

## Pending

- More thorough integration tests
- Advanced manager/admin role workflows (e.g., user management, approvals)
- Production-ready email provider and transaction scheduling
- Enhanced React UI with better styling and navigation
