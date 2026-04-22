-- Fase 1 MVP: tabla de pedidos (checkout simulado)
-- Ejecutar una sola vez en Supabase SQL Editor.

CREATE TABLE IF NOT EXISTS orders (
  id varchar(36) PRIMARY KEY,
  item_id varchar(80) NOT NULL,
  buyer_id varchar(36) NOT NULL,
  seller_id varchar(36) NOT NULL,
  item_title varchar(180) NOT NULL,
  item_image text NOT NULL,
  amount_eur double precision NOT NULL,
  delivery_method varchar(20) NOT NULL,
  shipping_full_name varchar(120),
  shipping_phone varchar(40),
  shipping_address_line1 varchar(220),
  shipping_city varchar(120),
  shipping_postal_code varchar(20),
  shipping_country varchar(80),
  pickup_city varchar(120),
  pickup_notes varchar(500),
  payment_brand varchar(30) NOT NULL,
  payment_last4 varchar(4) NOT NULL,
  status varchar(40) NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_orders_buyer_created_at
  ON orders (buyer_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_orders_seller_created_at
  ON orders (seller_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_orders_item
  ON orders (item_id);
