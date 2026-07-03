-- V6__monetary_columns_to_numeric.sql
-- Change monetary columns from DOUBLE PRECISION/FLOAT to NUMERIC(10,2)

ALTER TABLE products ALTER COLUMN price TYPE NUMERIC(10,2);

ALTER TABLE orders ALTER COLUMN delivery_fee TYPE NUMERIC(10,2);
ALTER TABLE orders ALTER COLUMN total_value TYPE NUMERIC(10,2);

ALTER TABLE payments ALTER COLUMN amount TYPE NUMERIC(10,2);
