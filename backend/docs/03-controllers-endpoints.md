# Controllers and Endpoints

This backend exposes a REST API under `/api/customers`. Below you’ll find each route with its purpose, inputs, and outputs. Swagger UI shows the same info with examples and lets you try requests.

## How things are structured (quick refresher)
- Controller (@RestController): receives HTTP requests, validates input, and calls the Service.
- Service: business logic and orchestration (exists/email, paging/search, etc.).
- Repository: persistence with Spring Data JPA.
- DTOs: API shapes for requests/responses (don’t expose JPA entities directly).

## Base path
All routes below are relative to `/api/customers`.

---

## List all (small lists)
- Method/Path: GET `/`
- Purpose: return all customers (useful for small datasets or quick checks).
- Response: 200 OK → array of `CustomerResponse`.

---

## Get by id
- Method/Path: GET `/{id}`
- Purpose: fetch a single customer by id.
- Path params: `id` (Long)
- Responses:
	- 200 OK → `CustomerResponse`
	- 404 Not Found

---

## Create
- Method/Path: POST `/`
- Purpose: create a new customer.
- Body: `CustomerRequest` (firstName, lastName, email, phone?, address?)
- Responses:
	- 201 Created → `CustomerResponse`
	- 400 Bad Request (validation)
	- 409 Conflict (email already exists)

Example body
```json
{
	"firstName": "Ana",
	"lastName": "García",
	"email": "ana.garcia@example.com",
	"phone": "123-4567",
	"address": "123 Main St"
}
```

---

## Update
- Method/Path: PUT `/{id}`
- Purpose: update an existing customer by id.
- Path params: `id` (Long)
- Body: `CustomerRequest` (same fields as create; full replace)
- Responses:
	- 200 OK → `CustomerResponse`
	- 404 Not Found
	- 409 Conflict (email already exists)

---

## Delete
- Method/Path: DELETE `/{id}`
- Purpose: delete a customer by id.
- Path params: `id` (Long)
- Responses:
	- 204 No Content
	- 404 Not Found

---

## Paged list
- Method/Path: GET `/page`
- Purpose: list customers with pagination and sorting.
- Query params:
	- `page` (0..N)
	- `size` (1..50; capped at 50)
	- `sort` (e.g., `lastName,ASC` or `createdAt,DESC`; can repeat)
- Responses:
	- 200 OK → `PageResponse<CustomerResponse>`
	- 400 Bad Request (invalid page/size/sort)

Example
```
GET /api/customers/page?page=0&size=10&sort=createdAt,DESC
```

---

## Paged search
- Method/Path: GET `/search/page`
- Purpose: search by text in firstName, lastName, email, phone, or address.
- Query params:
	- `q` (search text)
	- `page`, `size`, `sort` (same as above)
- Responses:
	- 200 OK → `PageResponse<CustomerResponse>`
	- 400 Bad Request (invalid page/size/sort)

Example
```
GET /api/customers/search/page?q=john&page=0&size=10&sort=lastName,ASC
```

---

## By email
- Method/Path: GET `/by-email?email=...`
- Purpose: lookup a customer by exact email.
- Responses:
	- 200 OK → `CustomerResponse`
	- 404 Not Found

---

## By phone
- Method/Path: GET `/by-phone?phone=...`
- Purpose: lookup a customer by exact phone.
- Responses:
	- 200 OK → `CustomerResponse`
	- 404 Not Found

---

## Exists by email
- Method/Path: GET `/exists/email/{email}`
- Purpose: check if a customer exists for the given email.
- Response: 200 OK → boolean

---

## Count by last name
- Method/Path: GET `/count/lastname/{lastName}`
- Purpose: count customers with a given last name.
- Response: 200 OK → number

---

## Stats
- Method/Path: GET `/stats`
- Purpose: aggregated stats (implementation-defined summary fields).
- Response: 200 OK → `CustomerStats`

---

## Deprecated routes (hidden from Swagger)
Kept for backward compatibility; prefer the paged search.
- GET `/search/{searchTerm}`
- GET `/search/firstname/{firstName}`
- GET `/search/lastname/{lastName}`
- GET `/search/contains/{name}`

---

## Notes on responses and errors
- Success responses return DTOs (`CustomerResponse`) or primitives (boolean/number) as documented.
- Errors follow a consistent JSON format with `timestamp`, `path`, `status`, `error`, `message`.
- Swagger UI shows example payloads for each route and the Schemas for request/response models.
