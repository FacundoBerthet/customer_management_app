# Error Handling

This backend returns a consistent JSON for errors so clients can handle them easily. Below you’ll see the format, when each status is used, and example payloads.

## Error format (ErrorResponse)

All errors follow this shape:

```json
{
	"timestamp": "2025-08-10T12:34:56.789Z",
	"path": "/api/customers/1",
	"status": 404,
	"error": "Not Found",
	"message": "Customer not found"
}
```

Fields
- `timestamp` : when the error happened
- `path`: the request path
- `status`: HTTP status code
- `error`: standard reason phrase
- `message`: human‑readable message

Model: see `ErrorResponse` class (documented with `@Schema`).

## Mappings and when they apply

The GlobalExceptionHandler sets these statuses:

- 400 Bad Request — Bean Validation failures (@Valid) in request body or params.
	- Returns the first field error message to keep it simple.
- 404 Not Found — resource doesn’t exist (the Service throws IllegalArgumentException).
- 409 Conflict — duplicate email (DuplicateEmailException at Service level).
- 500 Internal Server Error — any other unexpected exception.

## Examples

400 (validation error)
```json
{
	"timestamp": "2025-08-13T10:00:00Z",
	"path": "/api/customers",
	"status": 400,
	"error": "Bad Request",
	"message": "First name is required"
}
```

404 (not found)
```json
{
	"timestamp": "2025-08-13T10:00:00Z",
	"path": "/api/customers/999",
	"status": 404,
	"error": "Not Found",
	"message": "Customer not found"
}
```

409 (email exists)
```json
{
	"timestamp": "2025-08-13T10:00:00Z",
	"path": "/api/customers",
	"status": 409,
	"error": "Conflict",
	"message": "Email already exists"
}
```

500 (unexpected)
```json
{
	"timestamp": "2025-08-13T10:00:00Z",
	"path": "/api/customers/1",
	"status": 500,
	"error": "Internal Server Error",
	"message": "Unexpected error"
}
```

## Client tips

- Map statuses to UI messages: 400 (show field errors), 404 (record not found), 409 (email already used), 500 (try again).
- In forms, display the server’s `message` for precision (it already contains the first validation error).
- Log full `ErrorResponse` in the client for debugging when needed.
