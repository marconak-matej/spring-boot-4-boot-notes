-- V1.1: Create products table structure

CREATE SEQUENCE IF NOT EXISTS dbo.product_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE dbo.products
(
    id          BIGINT                      NOT NULL DEFAULT nextval('dbo.product_id_seq'),
    name        VARCHAR(200)                NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2)              NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE INDEX idx_products_name ON dbo.products (name);
