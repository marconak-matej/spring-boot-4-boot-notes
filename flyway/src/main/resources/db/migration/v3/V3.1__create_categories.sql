-- V3.1: Create categories table and relationship

ALTER TABLE dbo.products
    ADD COLUMN category VARCHAR(50);
