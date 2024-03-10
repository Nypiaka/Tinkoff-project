package edu.java.scrapper;

import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleTest extends IntegrationTest {

    @Test
    public void simpleTest() throws SQLException {
        var connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        );
        connection.createStatement().executeUpdate(
            "insert into links (link) values ('https://link.ru')"
        );
        var res = connection.createStatement().executeQuery(
            "select l.link from links l"
        );
        Assertions.assertTrue(res.next());
        Assertions.assertEquals("https://link.ru", res.getString(1));
        connection.createStatement().executeUpdate(
            "delete from links where link = 'https://link.ru'"
        );
        var newRes = connection.createStatement().executeQuery(
            "select count(*) from links l"
        );
        Assertions.assertTrue(newRes.next());
        Assertions.assertEquals(0, newRes.getInt(1));
    }
}
