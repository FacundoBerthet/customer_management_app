# Customer Management App

Simple full‑stack app to manage customers. The backend is Spring Boot with PostgreSQL and Flyway. The frontend is React (Vite) served by Nginx. You can run everything with Docker Compose.

![Home page](docs/images/home_page.png)

## Table of Contents

- [What you can do](#what-you-can-do)
- [Documentation](#documentation)
- [Tiny Quickstart](#tiny-quickstart)
- [Dev (without Docker)](#dev-without-docker)
- [Architecture](#architecture)
- [Screenshots](#screenshots)

## What you can do

- Create, edit, and delete customers
- List customers with pagination and sorting
- Search customers by name, email, phone, or address
- View full customer details
- Try the API in Swagger UI

## Documentation

- Frontend
  - [Frontend Overview](front/README.md)
  - [Frontend Documentation](front/docs/)

- Backend
  - [Backend Overview](backend/README.md)
  - [Backend Documentation](backend/docs/)

More pages (setup, API examples, Docker tips, troubleshooting) will be added later.

## Tiny Quickstart

Run the whole stack with Docker:

```bash
docker compose up -d --build
```

Open the app and API:
- Frontend: http://localhost:8081
- Swagger UI (API docs): http://localhost:8080/swagger-ui.html

Stop everything:

```bash
docker compose down
```

## Dev (without Docker)

Requirements: Java 17, Node.js, and a local PostgreSQL server.

1) Backend
- Edit `backend/src/main/resources/application-dev.properties` with your DB name, user, and password.
- Create the database (example: `customerdb`).
- Run the API:

```bash
cd backend
./mvnw spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui.html

2) Frontend
- Set API URL in `frontend/.env`:
  - `VITE_API_URL=http://localhost:8080/api`
- Start the dev server:

```bash
cd frontend
npm install
npm run dev
```

App (Vite dev): http://localhost:5173
