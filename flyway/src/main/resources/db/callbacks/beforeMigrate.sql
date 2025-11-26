-- beforeMigrate callback
-- Logs migration start

DO
$$
    BEGIN
        RAISE NOTICE '→ Starting Flyway migration at %', NOW();
    END
$$;
