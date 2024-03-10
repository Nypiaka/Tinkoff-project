package edu.java.dao;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class LinksDaoTest {

    private static JdbcDatabaseContainer<?> POSTGRES;

    private static void runMigrations(JdbcDatabaseContainer<?> c) {
        try {
            var jdbc =
                new JdbcConnection(DriverManager.getConnection(c.getJdbcUrl(), c.getUsername(), c.getPassword()));
            var db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbc);
            new Liquibase(
                "master.xml",
                new DirectoryResourceAccessor(Path.of("..", "migrations")),
                db
            ).update(new Contexts());
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
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var id = 1L;
        var link1 = "https://example.com/1";
        var link2 = "https://example.com/2";
        var link3 = "https://example.com/3";
        var links = List.of(link1, link2, link3);
        saveLinks(id, links);

        var retrievedLinks = linksDao.getList(id);

        Assertions.assertEquals(links, retrievedLinks);
    }

    @Test
    public void testSaveLink() throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var id = 1L;
        var link = "https://example.com/test";
        var content = "This is some test content.";

        var saved = linksDao.saveLink(id, link, content);

        Assertions.assertTrue(saved);

        var retrievedLinks = linksDao.getList(id);
        Assertions.assertEquals(List.of(link), retrievedLinks);

        var retrievedContent = linksDao.getLastUpdate(link);
        Assertions.assertEquals(content, retrievedContent);
    }

    @Test
    public void testRemoveLink() throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var id = 1L;
        var link = "https://example.com/test";
        var content = "This is some test content.";
        saveLink(id, link, content);

        var removed = linksDao.removeLink(id, link);

        Assertions.assertTrue(removed);

        var retrievedLinks = linksDao.getList(id);
        Assertions.assertEquals(List.of(), retrievedLinks);
    }

    @Test
    public void testUpdateLink() throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var id1 = 1L;
        var id2 = 2L;
        var link = "https://example.com/test";
        var content1 = "This is some test content for ID 1.";
        var content2 = "This is some test content for ID 2.";
        saveLink(id1, link, content1);

        var updated = linksDao.updateLink(List.of(id1, id2), link, content2);

        Assertions.assertTrue(updated);

        var retrievedLinks1 = linksDao.getList(id1);
        Assertions.assertEquals(List.of(link), retrievedLinks1);
        var retrievedContent1 = linksDao.getLastUpdate(link);
        Assertions.assertEquals(content2, retrievedContent1);

        var retrievedLinks2 = linksDao.getList(id2);
        Assertions.assertEquals(List.of(link), retrievedLinks2);
        var retrievedContent2 = linksDao.getLastUpdate(link);
        Assertions.assertEquals(content2, retrievedContent2);
    }

    @Test
    public void testRegisterChat() throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var id = 1L;

        var registered = linksDao.registerChat(id);

        Assertions.assertTrue(registered);

        var containsChat = linksDao.containsChat(id);
        Assertions.assertTrue(containsChat);
    }

    @Test
    public void testRemoveChat() throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var id = 1L;
        registerChat(id);

        var removed = linksDao.removeChat(id);

        Assertions.assertTrue(removed);

        var containsChat = linksDao.containsChat(id);
        Assertions.assertFalse(containsChat);
    }

    @Test
    public void testContainsChat() throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var id = 1L;

        var containsChat = linksDao.containsChat(id);
        Assertions.assertFalse(containsChat);

        registerChat(id);

        containsChat = linksDao.containsChat(id);
        Assertions.assertTrue(containsChat);
    }

    @Test
    public void testGetAllLinks() throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var link1 = "https://example.com/1";
        var link2 = "https://example.com/2";
        var link3 = "https://example.com/3";
        var links = List.of(link1, link2, link3);
        saveLinks(0L, links);

        var retrievedLinks = linksDao.getAllLinks();

        Assertions.assertEquals(new HashSet<>(links), new HashSet<>(retrievedLinks));
    }

    @Test
    public void testGetLastUpdate() throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var link = "https://example.com/test";
        var update1 = "This is the first update for " + link;
        var update2 = "This is the second update for " + link;
        save(link, update1);
        save(link, update2);

        var retrievedUpdate = linksDao.getLastUpdate(link);

        Assertions.assertEquals(update2, retrievedUpdate);
    }

    @Test
    public void testSave() throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        var link = "https://example.com/test";
        var update = "This is the update for " + link;

        var saved = linksDao.save(link, update);

        Assertions.assertTrue(saved);

        var retrievedUpdate = linksDao.getLastUpdate(link);
        Assertions.assertEquals(update, retrievedUpdate);
    }

    private void saveLinks(Long id, List<String> links) throws SQLException {
        for (var link : links) {
            saveLink(id, link, "");
        }
    }

    private void saveLink(Long id, String link, String content) throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        linksDao.saveLink(id, link, content);
    }

    private void save(String link, String update) throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        linksDao.save(link, update);
    }

    private void registerChat(Long id) throws SQLException {
        var linksDao = new LinksDao(POSTGRES.getJdbcUrl());
        linksDao.registerChat(id);
    }
}
