-- Verify that the CHECK constraint exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_products_status'
    ) THEN
        ALTER TABLE dbo.products
        ADD CONSTRAINT chk_products_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED'));
    END IF;
END$$;
