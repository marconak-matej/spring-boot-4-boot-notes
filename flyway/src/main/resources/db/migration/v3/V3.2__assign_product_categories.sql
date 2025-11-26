-- V3.2: Assign categories to existing products

UPDATE dbo.products
SET category = 'ELECTRONICS'
WHERE name = 'Laptop';

UPDATE dbo.products
SET category = 'PERIPHERALS'
WHERE name IN ('Mouse', 'Keyboard');
