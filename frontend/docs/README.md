# Frontend Docs

Landing page for the React + Vite documentation. Use this index to jump into specific topics.

## What you can do here
- Understand the app architecture and folder layout
- Set up local development and environment variables
- Learn how the app talks to the backend API
- Navigate routes and page components
- See how we style the UI and common patterns
- See how the Docker/Nginx image works for production

## Quick links

- [Overview](01-overview.md)
- [Dev Setup](02-dev-setup.md)
- [API Integration](03-api-integration.md)
- [Routing and Pages](04-routing-pages.md)
- [Styling and UI](05-styling-ui.md)
- [Docker and Nginx](06-docker-nginx.md)
- [Troubleshooting](07-troubleshooting.md)
- [Testing](08-testing.md)

## Using the app with the backend

- In dev, set `VITE_API_URL` in `.env` to your backend URL (e.g., `http://localhost:8080`).
- In Docker, the frontend is built with `VITE_API_URL=/api` and Nginx proxies `/api` to the backend.
