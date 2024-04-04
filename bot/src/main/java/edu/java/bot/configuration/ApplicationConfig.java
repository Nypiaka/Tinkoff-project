package edu.java.bot.configuration;

import edu.java.bot.bot.LinksRefreshCheckerBot;
import edu.java.bot.clients.ScrapperClient;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

@Validated
@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationConfig {
    @NotEmpty
    @Value("${app.telegram-token}")
    String telegramToken;

    @NotEmpty
    @Value("${server.link}")
    String baseUrl;

    @NotNull
    @Value("${app.ratelimit.capacity}")
    Long capacity;

    @NotNull
    @Value("${app.ratelimit.period}")
    Duration period;

    @Bean
    public LinksRefreshCheckerBot linksRefreshCheckerBot() {
        return new LinksRefreshCheckerBot(telegramToken);
    }

    @Bean
    public ScrapperClient scrapperClient() {
        return new ScrapperClient(WebClient.builder().baseUrl(baseUrl).build());
    }

    @Bean Bucket bucket() {
        return Bucket.builder().addLimit(
            Bandwidth.classic(
                capacity,
                Refill.intervally(capacity, period)
            )
        ).build();
    }
}
