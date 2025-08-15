# Database Migrations (Flyway)

This project uses Flyway to version the database schema. Flyway runs SQL files on startup.

- Files are in `src/main/resources/db/migration`.
- Names look like `V1__something.sql`, `V2__something.sql`.
- Flyway keeps a history in a table called `flyway_schema_history`.

How I add a change
1) Think about the change (for example: add a column).
2) Create a new file with the next version number. Example: `V3__add_status_to_customer.sql`.
3) Write the SQL in that file.
4) Start the app. Flyway will apply the new file.

What exists now
- `V1__create_customers.sql`: creates the `customer` table and indexes.
- `V2__timestamps_defaults.sql`: sets default values for timestamps and fills missing values.
- `V3__fix_customer_id_sequence.sql`: fixes auto-generated IDs in existing databases by adding a sequence default to `customer.id` and moving the sequence to the next correct value.

FAQ
- Does Flyway create the database? No. The database must exist and be reachable. Flyway creates/updates the schema (tables, indexes, constraints) inside it.
- What if my database already has tables? In dev, this project uses baseline-on-migrate. Flyway adopts the current schema and starts tracking from there.
- Can I change an old migration? It is better to create a new version (V3, V4, …). Flyway tracks checksums and warns if a past migration changes.
- What if a migration fails? Fix the SQL and run again. If a partial change happened, write a new migration to correct it.
