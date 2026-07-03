-- V7__add_cpf_unique.sql
-- Add UNIQUE constraint to users.cpf

ALTER TABLE users ADD CONSTRAINT uk_user_cpf UNIQUE (cpf);
