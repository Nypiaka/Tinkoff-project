package edu.java.configuration;

import edu.java.dao.JooqLinksDao;
import edu.java.service.JooqLinksService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
@RequiredArgsConstructor
public class JooqAccessConfiguration {

    private final JooqLinksDao jooqLinksDao;

    @Bean JooqLinksService jooqLinksService() {
        return new JooqLinksService(jooqLinksDao);
    }
}
