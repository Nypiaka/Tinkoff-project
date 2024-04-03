package edu.java.scrapper;

import edu.java.dao.JdbcLinksDao;
import edu.java.service.JdbcLinksService;
import edu.java.service.LinksService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class JdbcLinksServiceTest extends AbstractServiceTest {

    private JdbcLinksDao dao;

    @Override
    protected LinksService getService() {
        return new JdbcLinksService(new JdbcLinksDao(DATA));
    }

    @Override
    protected boolean containsLinkInChat(Long id, String link) {
        return dao.chatContainsLink(id, link);
    }

    @Override
    protected void saveLinks(Long id, List<String> links) {
        links.forEach(l -> saveLinkInChat(id, l));
    }

    @Override
    protected void saveLinkInChat(Long id, String link) {
        if (!dao.exists(link)) {
            dao.saveLink(link);
        }
        dao.saveLinkToChat(id, link);
    }

    @BeforeEach
    protected void before() {
        POSTGRES.close();
        POSTGRES.start();
        runMigrations(POSTGRES);
        dao = new JdbcLinksDao(DATA);
    }

}
