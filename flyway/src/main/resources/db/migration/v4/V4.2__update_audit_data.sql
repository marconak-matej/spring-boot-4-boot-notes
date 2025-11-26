-- V4.2: Update existing products with audit data

UPDATE dbo.products
SET created_by = 'system',
    updated_by = 'system'
WHERE created_by IS NULL;
