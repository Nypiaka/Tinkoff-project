package edu.java.configuration;

import edu.java.clients.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.retry.Restarter;
import edu.java.service.LinksService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ClientsConfig {
    private final LinksService linksService;

    private final ApplicationConfig config;

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
        return new BotClient(WebClient.builder().baseUrl("http://localhost:8090").build(), restarter());
    }

    @Bean Restarter restarter() {
        return new Restarter(
            config.backoff().policy(),
            config.backoff().maxAttempts(),
            config.backoff().delay(),
            config.backoff().supportedCodes()
        );
    }

    @Bean Bucket bucket() {
        return Bucket.builder().addLimit(
            Bandwidth.classic(
                config.rateLimit().capacity(),
                Refill.intervally(config.rateLimit().capacity(), config.rateLimit().period())
            )
        ).build();
    }

}
