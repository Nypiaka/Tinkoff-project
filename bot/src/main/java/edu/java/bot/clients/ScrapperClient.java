package edu.java.bot.clients;

import edu.java.utils.dto.AddLinkRequest;
import edu.java.utils.dto.ListLinksResponse;
import edu.java.utils.dto.RemoveLinkRequest;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ScrapperClient {

    private final WebClient webClient;

    private static final String CHAT_URI = "/chat-id/{id}";

    private static final String LINKS_URI = "/links";

    private static final String ID_HEADER = "Tg-Chat-Id";

    public ScrapperClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> registerChat(Long id) {
        return webClient.post()
            .uri(CHAT_URI, id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Void> removeChat(Long id) {
        return webClient.delete()
            .uri(CHAT_URI, id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<ListLinksResponse> getAllLinks(Long id) {
        return webClient.get()
            .uri(LINKS_URI)
            .header(ID_HEADER, String.valueOf(id))
            .retrieve()
            .bodyToMono(ListLinksResponse.class);
    }

    public Mono<Void> addLink(Long id, URI link) {
        AddLinkRequest request = new AddLinkRequest(link);
        return webClient.post()
            .uri(LINKS_URI)
            .header(ID_HEADER, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Void> removeLink(Long id, URI link) {
        RemoveLinkRequest request = new RemoveLinkRequest(link);
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS_URI)
            .header(ID_HEADER, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(Void.class);
    }
}
