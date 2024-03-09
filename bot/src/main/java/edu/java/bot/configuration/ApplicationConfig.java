package edu.java.bot.configuration;

import edu.java.bot.bot.LinksRefreshCheckerBot;
import edu.java.dao.LinksDao;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    @Value("${bot.key}")
    String telegramToken
) {

    @Bean
    public LinksRefreshCheckerBot linksRefreshCheckerBot() {
        return new LinksRefreshCheckerBot(telegramToken);
    }

    @Bean
    public LinksDao linksDao() {
        return new LinksDao();
    }
}
