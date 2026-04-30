ALTER TABLE items
  ADD COLUMN IF NOT EXISTS subcategoria VARCHAR(80);

UPDATE items
SET
  categoria = trim(split_part(categoria, '>', 1)),
  subcategoria = NULLIF(trim(split_part(categoria, '>', 2)), '')
WHERE categoria LIKE '%>%'
  AND (subcategoria IS NULL OR trim(subcategoria) = '');
