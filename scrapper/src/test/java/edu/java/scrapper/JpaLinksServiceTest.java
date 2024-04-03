package edu.java.scrapper;

import edu.java.dao.repository.JpaChatRepository;
import edu.java.dao.repository.JpaContentRepository;
import edu.java.dao.repository.JpaLinksRepository;
import edu.java.service.JpaLinksService;
import edu.java.service.LinksService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

//@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "app.database-access-type=jpa")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JpaLinksServiceTest extends AbstractServiceTest {
    @Autowired
    private JpaChatRepository jpaChatRepository;

    @Autowired
    private JpaLinksRepository jpaLinksRepository;

    @Autowired
    private JpaContentRepository jpaContentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void clear() {
        testEntityManager.clear();
        testEntityManager.flush();
    }

    @Override
    protected LinksService getService() {
        return new JpaLinksService(jpaLinksRepository, jpaChatRepository, jpaContentRepository);
    }

}
