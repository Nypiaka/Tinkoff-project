package edu.java.configuration;

import edu.java.retry.BackOffPolicy;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @NotNull
    Backoff backoff,
    @NotNull
    RateLimit rateLimit,
    @DefaultValue("https://api.github.com/repos/")
    String githubLink,
    @DefaultValue("https://api.stackexchange.com/2.3/questions/")
    String stackOverflowLink
) {
    public record Scheduler(boolean enable, @NotNull Integer updateTime, @NotNull Duration interval,
                            @NotNull Duration forceCheckDelay) {
    }

    public record Backoff(@NotNull BackOffPolicy policy, @NotNull Integer maxAttempts, @NotNull Duration delay,
                          @NotNull List<Integer> supportedCodes) {
    }

    public record RateLimit(@NotNull Long capacity, @NotNull Duration period) {

    }
}
