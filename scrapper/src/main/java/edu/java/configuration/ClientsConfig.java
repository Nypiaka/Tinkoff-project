package edu.java.configuration;

import edu.java.clients.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.retry.Restarter;
import edu.java.service.LinksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientsConfig {

    @Autowired
    private LinksService linksService;

    @Autowired
    ApplicationConfig config;

    @Bean GitHubClient gitHubClient() {
        return new GitHubClient(
            config.githubLink(),
            linksService,
            restarter()
        );
    }

    @Bean StackOverflowClient stackOverflowClient(ApplicationConfig config) {
        return new StackOverflowClient(
            config.stackOverflowLink(),
            linksService,
            restarter()
        );
    }

    @Bean BotClient botClient() {
        return new BotClient(WebClient.builder().baseUrl("http://localhost:8090").build());
    }

    @Bean Restarter restarter() {
        return new Restarter(
            config.backoff().policy(),
            config.backoff().maxAttempts(),
            config.backoff().delay(),
            config.backoff().supportedCodes()
        );
    }

}
