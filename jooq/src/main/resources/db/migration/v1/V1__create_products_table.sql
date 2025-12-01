CREATE SCHEMA IF NOT EXISTS dbo;

CREATE SEQUENCE IF NOT EXISTS dbo.product_id_seq START WITH 1 INCREMENT BY 1;

-- Main products table with all columns
CREATE TABLE dbo.products
(
    id             BIGINT                      NOT NULL DEFAULT nextval('dbo.product_id_seq'),
    name           VARCHAR(200)                NOT NULL,
    description    TEXT,
    price          DECIMAL(10, 2)              NOT NULL,
    stock_quantity INTEGER                     NOT NULL DEFAULT 0,
    sku            VARCHAR(50),
    category       VARCHAR(50),
    status         VARCHAR(20)                 NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT chk_products_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED'))
);

-- Create index for common queries
CREATE INDEX idx_products_status ON dbo.products(status);
CREATE INDEX idx_products_category ON dbo.products(category);
CREATE INDEX idx_products_sku ON dbo.products(sku);

-- Set sequence to start at 101 for testing purposes
ALTER SEQUENCE dbo.product_id_seq RESTART WITH 101;
