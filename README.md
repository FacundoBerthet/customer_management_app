# Customer Management App

This is a small Spring Boot REST API to manage customers. I built it to practice Java, Spring, PostgreSQL, and API documentation.

## What it uses
- Java 17
- Spring Boot 3.5 (Web, Data JPA, Validation)
- PostgreSQL
- Maven
- OpenAPI/Swagger UI (springdoc)
- Flyway for database migrations

## How to run (dev)
1) Make sure you have Java 17 and a PostgreSQL server running.
2) Create a database (example name: `customerdb`). Flyway will create the tables.
3) Set your database user and password in `src/main/resources/application-dev.properties`.
4) Start the app:

```bash
./mvnw spring-boot:run
```

5) Open Swagger UI in your browser:
- http://localhost:8080/swagger-ui.html

## Using Swagger (testing the API)
Swagger UI is the place where you can try the API without extra tools.
- You can see all endpoints, their methods (GET, POST, etc.), and paths.
- It shows the parameters you can send (like page, size, q) and the response codes.
- It shows the request and response models.
- You can click “Try it out”, fill the fields, and execute real requests. The response appears on the page.

## What the API does
- Create, read, update, and delete customers.
- List customers with pagination.
- Search customers by text (name, last name, email, phone, address).
- Look up a customer by email or phone.

For details and examples, see Swagger UI.

## Quick examples in Swagger
- List customers (paged):
  - Go to GET /api/customers/page
  - page = 0, size = 5, sort = id,desc
  - Click “Try it out” and then “Execute”
- Search customers:
  - Go to GET /api/customers/search/page
  - q = john, page = 0, size = 10
  - Execute and see results
- Lookup by email:
  - Go to GET /api/customers/by-email
  - email = john.doe@example.com
  - Execute

## Pagination parameters
- page: starts at 0
- size: suggested 1..50 (the API caps the size to 50)
- sort: example `id,desc` or `lastName,asc`

## Database and migrations
- The app uses Flyway. It runs SQL files from `src/main/resources/db/migration` when the app starts.
- Current migrations:
  - `V1__create_customers.sql`: creates the `customer` table and indexes.
  - `V2__timestamps_defaults.sql`: sets default timestamps and fills missing values.
  - `V3__fix_customer_id_sequence.sql`: ensures `customer.id` is auto-generated in existing databases (adds a sequence default and sets the next value correctly).

## Error responses (example)
When something goes wrong, the API returns a JSON like this:

```json
{
  "timestamp": "2025-08-10T12:34:56",
  "path": "/api/customers/999",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with ID: 999"
}
```

## Run tests
```bash
./mvnw test
```

### Tests and the database (rollback)
To keep things simple, tests run against the same "dev" database. We do not want test data to stay there.
- Test classes are annotated with `@Transactional`, so each test runs inside a transaction and Spring rolls it back at the end. That means no customers created by tests will remain in your database.
- This is the easiest way to keep your DB clean while still testing real behavior.
- Optional: if you prefer full isolation, create a separate "test" profile (`application-test.properties` + `@ActiveProfiles("test")`) pointing to another database. For this project, the transactional rollback approach is enough.

## Change profile
By default the app runs with the `dev` profile. To use another profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```
For `prod`, set environment variables for the database connection (DB_URL, DB_USER, DB_PASSWORD).

## Troubleshooting
- Database connection refused:
  - Check PostgreSQL is running.
  - Check the database name, user, and password.
  - Make sure the database exists and you can connect with another tool.
- Port 8080 already in use:
  - Stop the other app using that port, or start this app on another port:
  ```bash
  ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
  ```
