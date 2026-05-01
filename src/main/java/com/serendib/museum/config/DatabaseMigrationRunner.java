package com.serendib.museum.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Runs database schema setup and seed data insertion on application startup.
 * Creates lookup tables (categories, user_types) and migrates the
 * onboarding_responses table from string columns to foreign keys.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            createLookupTables();
            seedLookupData();
            migrateOnboardingResponses();
            log.info("Database migration runner completed successfully.");
        } catch (Exception e) {
            log.error("Error during database migration", e);
        }
    }

    private void createLookupTables() {
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS categories (" +
            "    id BIGSERIAL PRIMARY KEY," +
            "    name VARCHAR(100) NOT NULL UNIQUE" +
            ")"
        );

        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS user_types (" +
            "    id BIGSERIAL PRIMARY KEY," +
            "    name VARCHAR(100) NOT NULL UNIQUE" +
            ")"
        );

        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS onboarding_response_categories (" +
            "    response_id BIGINT NOT NULL," +
            "    category_id BIGINT NOT NULL," +
            "    PRIMARY KEY (response_id, category_id)," +
            "    CONSTRAINT fk_response FOREIGN KEY (response_id) REFERENCES onboarding_responses(id) ON DELETE CASCADE," +
            "    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE" +
            ")"
        );

        log.info("Lookup tables verified/created.");
    }

    private void seedLookupData() {
        String[] categories = {
            "Coins", "Ancient Artifacts", "Kandy Era", "Kings & Royalty",
            "Agriculture", "Statues", "Culture", "Technology",
            "Architecture", "Traditional Arts"
        };
        for (String name : categories) {
            jdbcTemplate.update(
                "INSERT INTO categories (name) VALUES (?) ON CONFLICT (name) DO NOTHING", name
            );
        }

        String[] userTypes = {
            "Student", "Teacher", "Professor", "Researcher",
            "Tourist", "History Enthusiast", "Local Visitor", "Other"
        };
        for (String name : userTypes) {
            jdbcTemplate.update(
                "INSERT INTO user_types (name) VALUES (?) ON CONFLICT (name) DO NOTHING", name
            );
        }

        log.info("Seed data verified/inserted.");
    }

    private void migrateOnboardingResponses() {
        // Drop old string columns if they still exist
        dropColumnIfExists("onboarding_responses", "user_type", "character varying");
        dropColumnIfExists("onboarding_responses", "interests", null);

        // Add user_type_id column if it doesn't exist (Hibernate ddl-auto may have added it)
        addColumnIfNotExists("onboarding_responses", "user_type_id", "BIGINT");

        // Add foreign key constraint if it doesn't exist
        addForeignKeyIfNotExists(
            "onboarding_responses", "fk_user_type",
            "user_type_id", "user_types", "id"
        );

        log.info("onboarding_responses table migration verified.");
    }

    private void dropColumnIfExists(String table, String column, String dataType) {
        try {
            String sql;
            if (dataType != null) {
                sql = "SELECT COUNT(*) FROM information_schema.columns " +
                      "WHERE table_name = ? AND column_name = ? AND data_type = ?";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, table, column, dataType);
                if (count != null && count > 0) {
                    jdbcTemplate.execute("ALTER TABLE " + table + " DROP COLUMN " + column);
                    log.info("Dropped column {}.{}", table, column);
                }
            } else {
                sql = "SELECT COUNT(*) FROM information_schema.columns " +
                      "WHERE table_name = ? AND column_name = ?";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, table, column);
                if (count != null && count > 0) {
                    jdbcTemplate.execute("ALTER TABLE " + table + " DROP COLUMN " + column);
                    log.info("Dropped column {}.{}", table, column);
                }
            }
        } catch (Exception e) {
            log.warn("Could not drop column {}.{}: {}", table, column, e.getMessage());
        }
    }

    private void addColumnIfNotExists(String table, String column, String type) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = ? AND column_name = ?",
                Integer.class, table, column
            );
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
                log.info("Added column {}.{}", table, column);
            }
        } catch (Exception e) {
            log.warn("Could not add column {}.{}: {}", table, column, e.getMessage());
        }
    }

    private void addForeignKeyIfNotExists(String table, String constraintName,
                                           String column, String refTable, String refColumn) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.table_constraints " +
                "WHERE constraint_name = ? AND table_name = ?",
                Integer.class, constraintName, table
            );
            if (count == null || count == 0) {
                jdbcTemplate.execute(
                    "ALTER TABLE " + table + " ADD CONSTRAINT " + constraintName +
                    " FOREIGN KEY (" + column + ") REFERENCES " + refTable + "(" + refColumn + ")"
                );
                log.info("Added foreign key {} on {}", constraintName, table);
            }
        } catch (Exception e) {
            log.warn("Could not add FK {}: {}", constraintName, e.getMessage());
        }
    }
}
