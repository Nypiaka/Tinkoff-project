package edu.java.bot.clients;

import edu.java.bot.dto.AddLinkRequest;
import edu.java.bot.dto.ApiErrorResponse;
import edu.java.bot.dto.ListLinksResponse;
import edu.java.bot.dto.RemoveLinkRequest;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperClient {

    private final WebClient webClient;

    private static final String CHAT_URI = "/chat-id/{id}";

    private static final String LINKS_URI = "/links";

    private static final String ID_HEADER = "Tg-Chat-Id";

    public ScrapperClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<ApiErrorResponse> registerChat(Long id) {
        return webClient.post()
            .uri(CHAT_URI, id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(ApiErrorResponse.class);
    }

    public Mono<ApiErrorResponse> removeChat(Long id) {
        return webClient.delete()
            .uri(CHAT_URI, id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(ApiErrorResponse.class);
    }

    public Mono<ResponseEntity<ListLinksResponse>> getAllLinks(Long id) {
        return webClient.get()
            .uri(LINKS_URI)
            .header(ID_HEADER, String.valueOf(id))
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .map(ResponseEntity::ok);
    }

    public Mono<ResponseEntity<?>> addLink(Long id, URI link) {
        AddLinkRequest request = new AddLinkRequest(link);
        return webClient.post()
            .uri(LINKS_URI)
            .header(ID_HEADER, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(ApiErrorResponse.class)
            .map(ResponseEntity::ok);
    }

    public Mono<ResponseEntity<ApiErrorResponse>> removeLink(Long id, URI link) {
        RemoveLinkRequest request = new RemoveLinkRequest(link);
        return webClient.delete()
            .uri(LINKS_URI)
            .header(ID_HEADER, String.valueOf(id))
            .retrieve()
            .bodyToMono(ApiErrorResponse.class)
            .map(ResponseEntity::ok);
    }
}
