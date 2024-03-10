package edu.java.configuration;

import edu.java.dao.LinksDao;
import jakarta.validation.constraints.NotNull;
import java.sql.SQLException;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @DefaultValue("https://api.github.com/repos/")
    String githubLink,
    @DefaultValue("https://api.stackexchange.com/2.3/questions/")
    String stackOverflowLink
) {

    @Bean
    public LinksDao linksDao() throws SQLException {
        return new LinksDao("jdbc:postgresql://localhost:5432/scrapper");
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }
}
