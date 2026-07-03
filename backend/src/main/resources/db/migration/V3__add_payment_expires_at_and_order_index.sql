-- V3__add_payment_expires_at_and_order_index.sql

ALTER TABLE payments ADD COLUMN expires_at TIMESTAMP;

CREATE INDEX idx_payments_order_id ON payments(order_id);
