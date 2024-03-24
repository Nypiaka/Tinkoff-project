package edu.java.configuration;

import edu.java.dao.repository.JpaChatRepository;
import edu.java.dao.repository.JpaContentRepository;
import edu.java.dao.repository.JpaLinksRepository;
import edu.java.scheduler.LinkUpdaterScheduler;
import edu.java.service.JpaLinksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Autowired @Lazy LinkUpdaterScheduler linkUpdaterScheduler;
    @Autowired JpaLinksRepository jpaLinksRepository;
    @Autowired JpaChatRepository jpaChatRepository;
    @Autowired JpaContentRepository jpaContentRepository;

    @Bean JpaLinksService jpaLinksService() {
        return new JpaLinksService(jpaLinksRepository, jpaChatRepository, jpaContentRepository, linkUpdaterScheduler);
    }
}
