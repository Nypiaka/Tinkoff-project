package edu.java.configuration;

import edu.java.dao.JdbcLinksDao;
import edu.java.scheduler.LinkUpdaterScheduler;
import edu.java.service.JdbcLinksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Autowired @Lazy LinkUpdaterScheduler linkUpdaterScheduler;

    @Autowired JdbcLinksDao jdbcLinksDao;

    @Bean JdbcLinksService jdbcLinksService() {
        return new JdbcLinksService(jdbcLinksDao, linkUpdaterScheduler);
    }
}
