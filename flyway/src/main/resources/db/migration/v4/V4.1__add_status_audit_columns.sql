-- V4.1: Add product status and audit columns

ALTER TABLE dbo.products
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE dbo.products
    ADD CONSTRAINT chk_products_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED'));

CREATE INDEX idx_products_status ON dbo.products (status);

-- Add audit columns
ALTER TABLE dbo.products
    ADD COLUMN created_by VARCHAR(100);

ALTER TABLE dbo.products
    ADD COLUMN updated_by VARCHAR(100);
