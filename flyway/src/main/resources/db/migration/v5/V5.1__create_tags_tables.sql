-- V5.1: Create tags and product_tags tables

CREATE SEQUENCE IF NOT EXISTS dbo.tag_id_seq START WITH 1 INCREMENT BY 1;

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

CREATE INDEX idx_product_tags_tag_id ON dbo.product_tags (tag_id);
