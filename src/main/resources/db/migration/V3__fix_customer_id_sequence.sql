-- Asegurar que la columna id de customer se autogenere por secuencia
-- Evita: ERROR: null value in column "id" of relation "customer" violates not-null constraint

-- 1) Crear secuencia si no existe
CREATE SEQUENCE IF NOT EXISTS customer_id_seq START WITH 1 INCREMENT BY 1;

-- 2) Usar la secuencia como DEFAULT de la columna id
ALTER TABLE customer
  ALTER COLUMN id SET DEFAULT nextval('customer_id_seq');

-- 3) Declarar ownership para mantenimiento en cascada
ALTER SEQUENCE customer_id_seq OWNED BY customer.id;

-- 4) Ajustar el valor actual de la secuencia al máximo id presente
-- Manejar tabla vacía vs con datos: si está vacía, próxima será 1; si no, próxima será MAX(id)+1
SELECT setval(
  'customer_id_seq',
  COALESCE((SELECT MAX(id) FROM customer), 1),
  (SELECT COUNT(*) > 0 FROM customer)
);
