package edu.java.configuration;

import edu.java.clients.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.service.JdbcLinksService;
import edu.java.service.JpaLinksService;
import edu.java.service.LinksService;
import edu.java.service.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientsConfig {

    @Autowired
    private JpaLinksService jpaLinksService;

    @Autowired
    private JdbcLinksService jdbcLinksService;

    @Autowired
    ApplicationConfig config;

    @Bean GitHubClient gitHubClient() {
        return new GitHubClient(
            config.githubLink(),
            linksService()
        );
    }

    @Bean StackOverflowClient stackOverflowClient(ApplicationConfig config) {
        return new StackOverflowClient(
            config.stackOverflowLink(),
            linksService()
        );
    }

    @Bean BotClient botClient() {
        return new BotClient(WebClient.builder().baseUrl("http://localhost:8090").build());
    }

    @Bean LinksService linksService() {
        return config.scheduler().serviceType() == ServiceType.JDBC ? jdbcLinksService : jpaLinksService;
    }
}
