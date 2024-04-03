package edu.java.configuration;

import edu.java.dao.JdbcLinksDao;
import edu.java.service.JdbcLinksService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
@RequiredArgsConstructor
public class JdbcAccessConfiguration {

    private final JdbcLinksDao jdbcLinksDao;

    @Bean JdbcLinksService jdbcLinksService() {
        return new JdbcLinksService(jdbcLinksDao);
    }
}
