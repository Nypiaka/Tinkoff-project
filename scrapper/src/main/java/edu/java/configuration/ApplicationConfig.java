package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @DefaultValue("https://api.github.com/repos/")
    String githubLink,
    @DefaultValue("https://api.stackexchange.com/2.3/questions/")
    String stackOverflowLink
) {
    public record Scheduler(boolean enable, @NotNull Integer updateTime, @NotNull Duration interval,
                            @NotNull Duration forceCheckDelay) {
    }
}
