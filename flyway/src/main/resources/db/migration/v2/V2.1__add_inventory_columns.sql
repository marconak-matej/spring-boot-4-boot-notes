-- V2.1: Add inventory tracking columns

ALTER TABLE dbo.products
    ADD COLUMN stock_quantity INTEGER NOT NULL DEFAULT 0;

ALTER TABLE dbo.products
    ADD COLUMN sku VARCHAR(50);

ALTER TABLE dbo.products
    ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE;

CREATE INDEX idx_products_sku ON dbo.products (sku);
