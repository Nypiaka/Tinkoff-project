package edu.java.retry;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

@Component
public class Restarter {
    private final BackOffPolicy policy;
    private final Integer maxAttempts;

    private final Duration delay;

    private final Set<Integer> supportedCodes;

    public Restarter(BackOffPolicy policy, Integer maxAttempts, Duration delay, List<Integer> supportedCodes) {
        this.policy = policy;
        this.maxAttempts = maxAttempts;
        this.delay = delay;
        this.supportedCodes = new HashSet<>(supportedCodes);
    }

    @SuppressWarnings("MagicNumber")
    public Retry getRetry() {
        return switch (policy) {
            case CONST -> Retry.fixedDelay(maxAttempts, delay).filter(this::supports);
            case LINEAR -> Retry.backoff(maxAttempts, delay).jitter(0.25d).maxBackoff(delay.multipliedBy(5))
                .filter(this::supports);
            case EXP -> Retry.backoff(maxAttempts, delay).filter(this::supports);
        };
    }

    private boolean supports(Throwable e) {
        if (!(e instanceof WebClientResponseException)) {
            return false;
        }
        return supportedCodes.contains(((WebClientResponseException) e).getStatusCode().value());
    }
}
