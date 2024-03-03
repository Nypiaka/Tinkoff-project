package edu.java.clients;

import edu.java.dto.handlers.ApiErrorResponse;
import edu.java.dto.handlers.LinkUpdate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {

    private final WebClient webClient;

    public BotClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<ApiErrorResponse> update(LinkUpdate request) {
        return webClient.post()
            .uri("/updates")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(ApiErrorResponse.class);
    }
}
