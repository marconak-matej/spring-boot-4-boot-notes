CREATE SEQUENCE IF NOT EXISTS dbo.product_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS dbo.tag_id_seq START WITH 1 INCREMENT BY 1;

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

CREATE TABLE dbo.tags
(
    id         BIGINT                      NOT NULL DEFAULT nextval('dbo.tag_id_seq'),
    name       VARCHAR(50)                 NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_tags PRIMARY KEY (id),
    CONSTRAINT uk_tags_name UNIQUE (name)
);

CREATE TABLE dbo.product_tags
(
    product_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    CONSTRAINT pk_product_tags PRIMARY KEY (product_id, tag_id),
    CONSTRAINT fk_product_tags_product FOREIGN KEY (product_id) REFERENCES dbo.products (id) ON DELETE CASCADE,
    CONSTRAINT fk_product_tags_tag FOREIGN KEY (tag_id) REFERENCES dbo.tags (id) ON DELETE CASCADE
);

CREATE INDEX idx_products_name ON dbo.products (name);
CREATE INDEX idx_products_sku ON dbo.products (sku);
CREATE INDEX idx_products_status ON dbo.products (status);
CREATE INDEX idx_product_tags_tag_id ON dbo.product_tags (tag_id);

INSERT INTO dbo.tags (name)
VALUES ('wireless'),
       ('portable'),
       ('gaming'),
       ('professional'),
       ('budget-friendly');

INSERT INTO dbo.product_tags (product_id, tag_id)
SELECT p.id, t.id
FROM dbo.products p,
     dbo.tags t
WHERE (p.name = 'Mouse' AND t.name IN ('wireless', 'budget-friendly'))
   OR (p.name = 'Laptop' AND t.name IN ('portable', 'professional'))
   OR (p.name = 'Keyboard' AND t.name IN ('gaming', 'professional'));
