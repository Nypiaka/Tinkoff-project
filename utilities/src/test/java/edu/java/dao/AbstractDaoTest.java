package edu.java.dao;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public abstract class AbstractDaoTest {

    private static JdbcDatabaseContainer<?> POSTGRES;

    protected abstract LinksDao getDao() throws SQLException;

    protected static DriverManagerDataSource DATA;

    private static void runMigrations(JdbcDatabaseContainer<?> c) {
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

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
    }

    @BeforeEach
    public void setUp() throws SQLException {
        POSTGRES.start();
        runMigrations(POSTGRES);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        POSTGRES.close();
    }

    @Test
    public void testGetList() throws SQLException {
        var linksDao = getDao();
        var id = 1L;
        var link1 = "https://example.com/1";
        var link2 = "https://example.com/2";
        var link3 = "https://example.com/3";
        var links = List.of(link1, link2, link3);
        saveLinks(id, links, getDao());

        var retrievedLinks = linksDao.getList(id);

        Assertions.assertEquals(new HashSet<>(links), new HashSet<>(retrievedLinks));
    }

    @Test
    public void testRemoveChat() throws SQLException {
        var linksDao = getDao();
        var id = 1L;
        linksDao.registerChat(id);

        var removed = linksDao.removeChat(id);

        Assertions.assertTrue(removed);

        var containsChat = linksDao.containsChat(id);
        Assertions.assertFalse(containsChat);
    }

    @Test
    public void testGetAllLinks() throws SQLException {
        var linksDao = getDao();
        var link1 = "https://example.com/1";
        var link2 = "https://example.com/2";
        var link3 = "https://example.com/3";
        var links = List.of(link1, link2, link3);
        saveLinks(0L, links, linksDao);

        var retrievedLinks = linksDao.getAllLinks();

        Assertions.assertEquals(new HashSet<>(links), new HashSet<>(retrievedLinks));
    }

    @Test
    public void testGetLastUpdate() throws SQLException {
        var linksDao = getDao();
        var link = "https://example.com/test";
        var update1 = "This is the first update for " + link;
        var update2 = "This is the second update for " + link;
        linksDao.saveLink(link);
        linksDao.updateContent(linksDao.getId(link), update1, new Date());
        linksDao.updateContent(linksDao.getId(link), update2, new Date());

        var retrievedUpdate = linksDao.getLastUpdate(link);

        Assertions.assertEquals(update2, retrievedUpdate);
    }

    @Test
    public void testSave() throws SQLException {
        var linksDao = getDao();
        var link = "https://example.com/test";
        var update = "This is the update for " + link;

        linksDao.saveLink(link);
        var saved = linksDao.updateContent(linksDao.getId(link), update, new Date());

        Assertions.assertTrue(saved);

        var retrievedUpdate = linksDao.getLastUpdate(link);
        Assertions.assertEquals(update, retrievedUpdate);
    }

    private void saveLinks(Long id, List<String> links, LinksDao linksDao) throws SQLException {
        for (var link : links) {
            linksDao.saveLink(link);
            linksDao.saveLinkToChat(id, link);
        }
    }

}
