ALTER TABLE items
  ADD COLUMN IF NOT EXISTS subcategoria VARCHAR(80);

ALTER TABLE items
  ADD COLUMN IF NOT EXISTS genero VARCHAR(20);

UPDATE items
SET genero = COALESCE(NULLIF(trim(genero), ''), 'Unisex')
WHERE genero IS NULL OR trim(genero) = '';

UPDATE items
SET
  categoria = trim(split_part(categoria, '>', 1)),
  subcategoria = NULLIF(trim(split_part(categoria, '>', 2)), '')
WHERE categoria LIKE '%>%'
  AND (subcategoria IS NULL OR trim(subcategoria) = '');

CREATE TABLE IF NOT EXISTS password_reset_tokens (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  token_hash VARCHAR(128) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_token_hash ON password_reset_tokens (token_hash);
