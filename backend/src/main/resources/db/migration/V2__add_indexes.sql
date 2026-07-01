-- V2__add_indexes.sql
-- Add missing indexes and foreign keys for performance and data integrity

CREATE INDEX idx_orders_customer_id ON orders(customer_id);
ALTER TABLE orders ADD CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES users(id);

CREATE INDEX idx_deliveries_courier_id ON deliveries(courier_id);
CREATE INDEX idx_deliveries_status ON deliveries(status, courier_id);

CREATE INDEX idx_products_establishment_id ON products(establishment_id);

CREATE UNIQUE INDEX idx_payments_transaction_id ON payments(transaction_id);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_name ON user_roles(role_name);
