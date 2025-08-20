# Pagination, Search, and Sorting

This API provides server‑side pagination and sorting for customer lists, and a text search that scans multiple fields. These features keep responses fast and predictable, even with large datasets.

## Concepts

- Pagination: split results into pages using `page` (0‑based) and `size`.
- Sorting: order results by one or more fields using `sort=field,ASC|DESC`.
- Search: filter results by a free‑text query `q` across name, email, phone, and address.

## Endpoints

- Paged list: `GET /api/customers/page`
- Paged search: `GET /api/customers/search/page`

Both endpoints accept the same paging/sorting parameters; the search endpoint also requires `q`.

## Parameters and defaults

- `page`: integer, 0‑based. Default: 0 (first page).
- `size`: integer, number of items per page. Default: 10. Max: 50 (larger values are capped server‑side).
- `sort`: string, format `field,ASC|DESC`. Default: `id,DESC`.
	- You can repeat `sort` to sort by multiple fields (e.g., `sort=lastName,ASC&sort=firstName,DESC`).
	- Case‑insensitive for direction (`asc`/`ASC`).
- `q` (search endpoint only): free text (will match firstName, lastName, email, phone, address).

## Supported sort fields

Common sortable fields: `id`, `firstName`, `lastName`, `email`, `createdAt`, `updatedAt`.

## Response model

Both endpoints return a page wrapper containing metadata and the list of items:

```json
{
	"content": [ { /* CustomerResponse */ } ],
	"page": 0,
	"size": 10,
	"totalElements": 42,
	"totalPages": 5,
	"first": true,
	"last": false
}
```

Each item in `content` is a `CustomerResponse` with:
`id, firstName, lastName, email, phone, address, createdAt, updatedAt`.

## Examples

- First page, 10 per page, default sort (id desc):
```
GET /api/customers/page?page=0&size=10
```

- Second page (page=1), sort by lastName ascending then firstName descending:
```
GET /api/customers/page?page=1&size=10&sort=lastName,ASC&sort=firstName,DESC
```

- Search for "john", keep default paging, sort by createdAt desc:
```
GET /api/customers/search/page?q=john&sort=createdAt,DESC
```

- Search by part of email domain:
```
GET /api/customers/search/page?q=gmail.com&page=0&size=20
```

- Search by address substring (URL‑encode spaces):
```
GET /api/customers/search/page?q=742%20Evergreen
```

## Errors and limits

- Invalid parameters (e.g., negative page, bad sort format) return `400 Bad Request` with an error body.
- `size` above 50 is capped to 50 to protect the server.

## Tips

- Use zero‑based pages in clients (page 0 is the first page).
- Prefer the paged endpoints instead of fetching all records.
- Combine search + sorting for UX (e.g., `q=john&sort=lastName,ASC`).
- For stable order, always specify an explicit sort (otherwise defaults apply).
