package edu.java.scrapper;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class IntegrationTest {
    public static PostgreSQLContainer<?> POSTGRES;

    protected static DriverManagerDataSource DATA;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
    }

    @BeforeAll
    protected static void beforeAll() {
        POSTGRES.start();
        runMigrations(POSTGRES);
    }

    @AfterAll
    protected static void afterAll() {
        POSTGRES.close();
    }

    @BeforeEach
    protected void beforeEach() {
    }

    @AfterEach
    protected void afterEach() {
    }

    static void runMigrations(JdbcDatabaseContainer<?> c) {
        try {
            var jdbc =
                new JdbcConnection(DriverManager.getConnection(c.getJdbcUrl(), c.getUsername(), c.getPassword()));
            var db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbc);
            new Liquibase(
                "liquibase.xml",
                new DirectoryResourceAccessor(Path.of("..", "migrations")),
                db
            ).update(new Contexts());
            DATA = new DriverManagerDataSource();
            DATA.setUrl(POSTGRES.getJdbcUrl());
            DATA.setUsername(POSTGRES.getUsername());
            DATA.setPassword(POSTGRES.getPassword());
            DATA.setDriverClassName(POSTGRES.getDriverClassName());
        } catch (SQLException | LiquibaseException | FileNotFoundException ignored) {
        }
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
