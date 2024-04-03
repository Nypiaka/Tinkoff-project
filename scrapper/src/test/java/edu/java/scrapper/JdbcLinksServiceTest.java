package edu.java.scrapper;

import edu.java.dao.JdbcLinksDao;
import edu.java.service.JdbcLinksService;
import edu.java.service.LinksService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class JdbcLinksServiceTest extends AbstractServiceTest {

    @Override
    protected LinksService getService() {
        return new JdbcLinksService(new JdbcLinksDao(DATA));
    }

    @BeforeEach
    protected void before() {
        POSTGRES.close();
        POSTGRES.start();
        runMigrations(POSTGRES);
    }

}
