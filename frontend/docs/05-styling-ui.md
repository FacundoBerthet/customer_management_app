# Styling and UI

How we style the app and common UI patterns.

## Approach

- Base styles in `src/index.css` and `src/App.css`.
- Reusable components in `src/components/` (buttons, inputs, layout blocks).
- Keep forms controlled; show inline errors near fields.

## Components (examples)

- CustomerForm.jsx
	- Controlled inputs for firstName, lastName, email, phone, address.
	- Submit triggers create/update via API client.
	- Show backend validation errors mapped to fields.
- CustomerList.jsx
	- Table with pagination controls, sort indicators.
	- Quick search input bound to backend search endpoint.

## UI patterns

- Feedback
	- Success: toast/banner after create/update/delete.
	- Errors: inline messages for field errors, toast for global errors.
- Empty and loading states
	- Skeletons or simple placeholders when loading.
	- Helpful empty-state message when no results.
- Responsive
	- Stack layout on small screens; ensure tables scroll if needed.
