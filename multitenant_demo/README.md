# Multi-Tenant Spring Boot Application

A Spring Boot application that implements schema-based multi-tenancy, allowing data isolation between different tenants in a PostgreSQL database.

## Features

- Schema-based multi-tenancy
- JWT authentication and authorization
- RESTful API endpoints
- Tenant-specific data isolation
- Exception handling with structured responses

## Setup Instructions

### Prerequisites

- JDK 17 or later
- PostgreSQL 12 or later
- Maven or Gradle

### Database Setup

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE user_db;
   ```

2. The application will automatically create schemas for tenants when they're registered.

### Configuration

1. Configure database connection in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/user_db
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

2. JWT configuration:
   ```properties
   security.jwt.secret-key=your-secret-key
   security.jwt.expiration-time=9000000
   ```

### Building and Running

```bash
# Using Maven
mvn clean install
mvn spring-boot:run

# Using Gradle
./gradlew clean build
./gradlew bootRun
```

### API Usage

All API requests must include the `X-Tenant-ID` header to specify which tenant's schema to use:


# API Documentation

## Authentication

### Register a New User
- **URL**: `/api/v1/auth/signup`
- **Method**: `POST`
- **Auth Required**: No
- **Headers**: `X-Tenant-ID: {tenantId}`
- **Request Body**:
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER"
  }
  ```

### User Login
- **URL**: `/api/v1/auth/login`
- **Method**: `POST`
- **Auth Required**: No
- **Headers**: `X-Tenant-ID: {tenantId}`
- **Request Body**:
  ```json
  {
    "email": "john@example.com",
    "password": "password123"
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```
- **Error Response**: `401 Unauthorized`
  ```json
  {
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid credentials",
    "timestamp": "2023-06-15T10:15:30Z"
  }
  ```

## Tenant Management

### Create Tenant
- **URL**: `/api/tenants`
- **Method**: `POST`
- **Auth Required**: Yes (ADMIN role)
- **Headers**:
    - `Authorization: Bearer {token}`
- **Request Body**:
  ```json
  {
    "tenantId": "tenant1"
  }
  ```
- **Success Response**: `201 Created`
  ```
  Tenant created successfully: tenant1
  ```
- **Error Response**: `409 Conflict`
  ```
  Tenant already exists: tenant1
  ```

### Get All Tenants
- **URL**: `/api/tenants`
- **Method**: `GET`
- **Auth Required**: Yes (ADMIN role)
- **Headers**:
    - `Authorization: Bearer {token}`
- **Success Response**: `200 OK`
  ```json
  ["public", "tenant1", "tenant2"]
  ```

## Message Management

### Get Messages by Thread ID
- **URL**: `/api/messages/thread/{threadId}`
- **Method**: `GET`
- **Auth Required**: Yes
- **Headers**:
    - `Authorization: Bearer {token}`
    - `X-Tenant-ID: {tenantId}`
- **Success Response**: `200 OK`
  ```json
  [
    {
      "id": 1,
      "threadId": 1,
      "userId": 1,
      "content": "Hello world!",
      "tenantId": "tenant1"
    },
    {
      "id": 2,
      "threadId": 1,
      "userId": 2,
      "content": "Welcome to the discussion!",
      "tenantId": "tenant1"
    }
  ]
  ```

### Create Message
- **URL**: `/api/messages`
- **Method**: `POST`
- **Auth Required**: Yes
- **Headers**:
    - `Authorization: Bearer {token}`
    - `X-Tenant-ID: {tenantId}`
- **Request Body**:
  ```json
  {
    "threadId": 1,
    "userId": 1,
    "content": "This is a new message"
  }
  ```
- **Success Response**: `201 Created`
  ```json
  {
    "id": 3,
    "threadId": 1,
    "userId": 1,
    "content": "This is a new message",
    "tenantId": "tenant1"
  }
  ```

All API endpoints may return these error responses:

### 400 Bad Request
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request parameters",
  "timestamp": "2023-06-15T10:40:30Z"
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required",
  "timestamp": "2023-06-15T10:41:30Z"
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "timestamp": "2023-06-15T10:42:30Z"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "timestamp": "2023-06-15T10:43:30Z"
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "timestamp": "2023-06-15T10:44:30Z"
}
```
```