# Customer Management Application

A comprehensive Spring Boot application for managing customer data with a complete REST API, data persistence, and validation features.

## Overview

This project demonstrates a full-stack Spring Boot application implementing modern enterprise patterns including JPA for data persistence, Bean Validation for data integrity, and RESTful web services for client interaction.

## Technologies Used

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Data JPA**
- **Spring Web**
- **Spring Boot Validation**
- **H2 Database** (in-memory)
- **Maven** (build tool)

## Features Implemented

### Data Model
- Customer entity with comprehensive fields including personal information, contact details, and audit timestamps
- Automatic timestamp management for creation and modification tracking
- Bean Validation annotations for data integrity

### Data Persistence
- JPA repository with CRUD operations
- Custom query methods using Spring Data JPA naming conventions
- Advanced search capabilities including partial text matching and case-insensitive searches
- Native SQL queries for complex operations
- Support for existence checks and aggregate functions

### REST API
Complete RESTful web service supporting:
- **GET** `/api/customers` - Retrieve all customers
- **GET** `/api/customers/{id}` - Retrieve customer by ID
- **POST** `/api/customers` - Create new customer
- **PUT** `/api/customers/{id}` - Update existing customer
- **DELETE** `/api/customers/{id}` - Delete customer
- **GET** `/api/customers/search/firstname/{firstName}` - Search by first name
- **GET** `/api/customers/search/lastname/{lastName}` - Search by last name
- **GET** `/api/customers/search/contains/{name}` - Search by name containing text
- **GET** `/api/customers/exists/email/{email}` - Check email existence
- **GET** `/api/customers/count/lastname/{lastName}` - Count customers by last name
- **GET** `/api/customers/stats` - Retrieve application statistics

### Validation
- Server-side validation using Bean Validation annotations
- Email format validation
- Required field validation
- String length constraints
- Phone number pattern validation

### Database Features
- Automatic table creation and schema management
- Optimized queries for performance
- Support for both JPQL and native SQL queries
- Database-level constraints and indexing

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/customer_management_app/
│   │       ├── Customer.java                    # Entity model
│   │       ├── CustomerRepository.java          # Data access layer
│   │       └── CustomerManagementAPP.java       # REST controller & main class
│   └── resources/
│       └── application.properties              # Configuration
└── test/
    └── java/
        └── com/example/customer_management_app/
            └── CustomerManagementAppApplicationTests.java
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Testing the API

You can test the REST endpoints using curl or any HTTP client:

```bash
# Get all customers
curl http://localhost:8080/api/customers

# Create a new customer
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john.doe@example.com"}'

# Get customer by ID
curl http://localhost:8080/api/customers/1

# Search customers by last name
curl http://localhost:8080/api/customers/search/lastname/Doe
```

## API Documentation

### Customer Object Structure

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john.doe@example.com",
  "phone": "555-1234",
  "address": "123 Main St",
  "createdAt": "2025-08-01T19:47:36.511651",
  "updatedAt": "2025-08-01T19:47:36.511651"
}
```

### Response Codes
- `200 OK` - Successful retrieval or update
- `201 Created` - Successful creation
- `204 No Content` - Successful deletion
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found

## Database Schema

The application uses an in-memory H2 database with the following customer table structure:

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | Primary Key, Auto-increment |
| first_name | VARCHAR(255) | Not null, 2-40 characters |
| last_name | VARCHAR(255) | Not null, 2-40 characters |
| email | VARCHAR(255) | Not null, Unique, Valid email format |
| phone | VARCHAR(255) | Optional, Pattern: XXX-XXXX |
| address | VARCHAR(255) | Optional, Max 100 characters |
| created_at | TIMESTAMP | Auto-generated |
| updated_at | TIMESTAMP | Auto-updated |

## Development Notes

- The application uses constructor-based dependency injection
- Validation is handled through Bean Validation annotations
- The repository layer uses Spring Data JPA for automatic query generation
- Native SQL queries are used for complex aggregation operations
- CORS is enabled for cross-origin requests

## Future Enhancements

Potential areas for expansion:
- Service layer implementation for business logic separation
- Exception handling with custom error responses
- Unit and integration testing
- Relationship mapping with other entities
- Frontend implementation
- Security and authentication
- Database migration to persistent storage

## Build Information

- **Version**: 0.0.1-SNAPSHOT
- **Java Version**: 17
- **Spring Boot Version**: 3.5.4
- **Build Tool**: Maven
