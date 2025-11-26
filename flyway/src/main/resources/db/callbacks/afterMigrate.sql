-- afterMigrate callback
-- Logs successful migration completion

DO
$$
    BEGIN
        RAISE NOTICE '✓ Migration completed successfully at %', NOW();
    END
$$;
