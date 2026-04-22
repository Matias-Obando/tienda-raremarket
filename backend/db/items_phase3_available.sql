-- Fase 3: disponibilidad de productos
-- Evita errores 500 en /api/items tras introducir el campo available en el modelo.

ALTER TABLE IF EXISTS items
  ADD COLUMN IF NOT EXISTS available boolean;

UPDATE items
SET available = true
WHERE available IS NULL;

ALTER TABLE IF EXISTS items
  ALTER COLUMN available SET DEFAULT true,
  ALTER COLUMN available SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_items_available_created_at
  ON items (available, created_at DESC);
