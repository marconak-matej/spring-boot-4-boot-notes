-- V2.2: Update existing products with inventory data

UPDATE dbo.products
SET stock_quantity = 100,
    sku            = 'PROD-' || LPAD(id::TEXT, 5, '0'),
    updated_at     = NOW()
WHERE id <= 3;
