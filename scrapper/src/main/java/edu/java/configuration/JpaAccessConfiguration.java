package edu.java.configuration;

import edu.java.dao.repository.JpaChatRepository;
import edu.java.dao.repository.JpaContentRepository;
import edu.java.dao.repository.JpaLinksRepository;
import edu.java.service.JpaLinksService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
@RequiredArgsConstructor
public class JpaAccessConfiguration {
    private final JpaLinksRepository jpaLinksRepository;
    private final JpaChatRepository jpaChatRepository;
    private final JpaContentRepository jpaContentRepository;

    @Bean JpaLinksService jpaLinksService() {
        return new JpaLinksService(jpaLinksRepository, jpaChatRepository, jpaContentRepository);
    }
}
