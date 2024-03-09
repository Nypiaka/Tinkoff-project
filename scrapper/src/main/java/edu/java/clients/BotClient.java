package edu.java.clients;

import edu.java.utils.dto.ApiErrorResponse;
import edu.java.utils.dto.LinkUpdate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BotClient {

    private final WebClient webClient;

    @Value("${server.link}")
    private String baseUrl;

    public BotClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
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
