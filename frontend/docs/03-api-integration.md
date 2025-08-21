# API Integration

This page explains how the frontend calls the backend API: base URL selection, request helpers, error handling, and paging/search patterns. At the end, we break a function line by line.

## Base URL and endpoints

- The base comes from `VITE_API_URL` (dev) or falls back to `/api` (Docker/Nginx):

```js
const API_BASE = import.meta.env.VITE_API_URL || '/api';
const BASE_URL = `${API_BASE.replace(/\/$/, '')}/customers`;
```

- All customer endpoints are mounted under `/customers`.

## Request helper

We use a small wrapper to fetch JSON and surface API errors consistently:

```js
async function fetchJson(url, options = {}) {
	const res = await fetch(url, options);
	if (!res.ok) {
		const error = await res.json().catch(() => ({}));
		throw { status: res.status, ...error };
	}
	return res.json();
}
```

- Non-2xx responses are converted into thrown objects with `status` and any error body (matching the backend ErrorResponse).

## Common operations

- List all: `GET /api/customers`
- Get one: `GET /api/customers/{id}`
- Create: `POST /api/customers`
- Update: `PUT /api/customers/{id}`
- Delete: `DELETE /api/customers/{id}`

## Paging and search

- Paged list: `GET /api/customers/page?page=0&size=10&sort=id,DESC`
- Paged search: `GET /api/customers/search/page?q=...&page=0&size=10&sort=id,DESC`
- The client uses `URLSearchParams` to build query strings safely.

```js
const params = new URLSearchParams({ page, size, sort });
fetchJson(`${BASE_URL}/page?${params}`);
```

## Error handling in UI

- Catch thrown errors from `fetchJson`:

```ts
try {
	const page = await getCustomersPaged(0, 10, 'id,DESC');
} catch (e) {
	// e.status (e.g., 400, 404, 409) and fields from backend ErrorResponse
	// Show toast or inline message
}
```

## Line-by-line: example of a function

We’ll break down `searchCustomersPaged` from `src/api/client.js`:

```js
export async function searchCustomersPaged(q, page = 0, size = 10, sort = 'id,DESC') {
	// GET /api/customers/search/page?q=...&page=0&size=10&sort=id,DESC
	const params = new URLSearchParams({ q, page, size, sort });
	return fetchJson(`${BASE_URL}/search/page?${params}`);
}
```

Explanation
- `export async function searchCustomersPaged(q, page = 0, size = 10, sort = 'id,DESC') {`
	- Exports an async function to perform a server-side search with pagination.
	- `q` is the search query (free text matching first/last name, etc.).
	- Defaults: first page (0), size 10, sorted by id descending.
- Comment shows exact HTTP shape for quick reference.
- `const params = new URLSearchParams({ q, page, size, sort });`
	- Builds a URL-encoded query string safely (handles escaping, types).
- `return fetchJson(`{BASE_URL}/search/page?${params}`);`
	- Calls the common helper; any non-2xx will throw with status and error payload.
