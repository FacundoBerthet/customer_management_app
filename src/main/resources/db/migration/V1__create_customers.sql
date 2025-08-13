-- =============================================
-- V1: Esquema inicial de la tabla de clientes
-- ---------------------------------------------
-- ¿Qué hace esta migración?
-- - Crea la tabla customer con sus columnas
-- - Define restricciones NOT NULL donde aplica
-- - Agrega la restricción UNIQUE para email
-- - Crea índices útiles para búsquedas frecuentes
-- =============================================

-- NOTA: Flyway ejecuta estos scripts al iniciar la app.
--      El orden se define por el prefijo V{n}__*.sql
--      y mantiene un historial en la tabla flyway_schema_history.

CREATE TABLE IF NOT EXISTS customer (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(40) NOT NULL,
    last_name VARCHAR(40) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    address VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Evitar correos duplicados a nivel de base de datos
CREATE UNIQUE INDEX IF NOT EXISTS ux_customer_email ON customer (email);

-- Índices para acelerar búsquedas por nombre/apellido (case-insensitive via LOWER)
CREATE INDEX IF NOT EXISTS ix_customer_first_name_lower ON customer (LOWER(first_name));
CREATE INDEX IF NOT EXISTS ix_customer_last_name_lower ON customer (LOWER(last_name));

-- Índice para búsquedas y lookups por teléfono
CREATE INDEX IF NOT EXISTS ix_customer_phone ON customer (phone);
