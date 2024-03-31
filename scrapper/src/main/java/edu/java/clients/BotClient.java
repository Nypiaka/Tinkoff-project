package edu.java.clients;

import edu.java.retry.Restarter;
import edu.java.utils.dto.LinkUpdate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BotClient {

    private final WebClient webClient;

    private final Restarter restarter;

    public BotClient(WebClient webClient, Restarter restarter) {
        this.webClient = webClient;
        this.restarter = restarter;
    }

    public Mono<Void> update(LinkUpdate request) {
        return webClient.post()
            .uri("/updates")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(Void.class)
            .retryWhen(restarter.getRetry());
    }
}
