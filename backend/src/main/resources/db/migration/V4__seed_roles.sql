-- V4__seed_roles.sql
-- Seed roles that are required for user registration in any environment.

INSERT INTO roles (name) VALUES ('USER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('RESTAURANT')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('DELIVERY')
ON CONFLICT (name) DO NOTHING;
