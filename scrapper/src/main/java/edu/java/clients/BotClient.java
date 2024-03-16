package edu.java.clients;

import edu.java.utils.dto.ApiErrorResponse;
import edu.java.utils.dto.LinkUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BotClient {

    private final WebClient webClient;

    public BotClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ResponseEntity<?>> update(LinkUpdate request) {
        return webClient.post()
            .uri("/updates")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(ApiErrorResponse.class)
            .map(ResponseEntity::ok);
    }
}
