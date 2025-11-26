-- V5.3: Tag existing products

INSERT INTO dbo.product_tags (product_id, tag_id)
SELECT p.id, t.id
FROM dbo.products p,
     dbo.tags t
WHERE (p.name = 'Mouse' AND t.name IN ('wireless', 'budget-friendly'))
   OR (p.name = 'Laptop' AND t.name IN ('portable', 'professional'))
   OR (p.name = 'Keyboard' AND t.name IN ('gaming', 'professional'));
