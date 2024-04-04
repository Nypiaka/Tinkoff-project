package edu.java.scrapper;

import edu.java.dao.domain.ChatsToLinks;
import edu.java.dao.domain.Link;
import edu.java.dao.repository.JpaChatRepository;
import edu.java.dao.repository.JpaContentRepository;
import edu.java.dao.repository.JpaLinksRepository;
import edu.java.service.JpaLinksService;
import edu.java.service.LinksService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

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

    @Override
    protected boolean containsLinkInChat(Long id, String link) {
        return jpaChatRepository.existsByChatIdAndLinkId(
            id,
            jpaLinksRepository.findByLink(link).orElseThrow().getId()
        );
    }

    @Override
    protected void saveLinks(Long id, List<String> links) {
        links.forEach(l -> {
            saveLinkInChat(id, l);
        });
    }

    @Override
    protected void saveLinkInChat(Long id, String link) {
        Link lk;
        if (!jpaLinksRepository.existsByLink(link)) {
            lk = jpaLinksRepository.save(new Link(link));
        } else {
            lk = jpaLinksRepository.findByLink(link).orElseThrow();
        }
        jpaChatRepository.save(new ChatsToLinks(id, lk.getId()));
    }

}
