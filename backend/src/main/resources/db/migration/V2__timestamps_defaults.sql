-- =============================================
-- V2: Defaults y saneamiento de timestamps
-- ---------------------------------------------
--
-- ¿Qué hace?
-- 1) Define DEFAULT NOW() para created_at y updated_at
--    (si alguien inserta filas fuera de la app, la BD
--     igual completa los timestamps).
-- 2) Backfill: si hubiera valores nulos de runs anteriores,
--    los completa con NOW() para evitar inconsistencias.
--
-- Importante:
-- - No forzamos NOT NULL para no chocar con la validación
--   de Hibernate (ddl-auto=validate) ni con las entidades.
-- - No se pierden datos. Solo se actualizan nulos.
-- =============================================

ALTER TABLE customer
  ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE customer
  ALTER COLUMN updated_at SET DEFAULT NOW();

UPDATE customer
SET
  created_at = COALESCE(created_at, NOW()),
  updated_at = COALESCE(updated_at, NOW());
