package edu.java.clients;

import edu.java.service.LinksService;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class AbstractClient<T> {
    private final WebClient webClient;

    private final LinksService linksService;

    protected abstract void log(String line);

    private boolean onReceipt(String s, T dto) {
        if (dto == null) {
            linksService.update(s, "Error in last update");
            return false;
        }
        var lastModified = linksService.getLastUpdate(s);
        if (lastModified == null || !lastModified.equals(dtoToString(dto))) {
            linksService.update(s, dtoToString(dto));
            log("Updates by link: " + s + ": " + dtoToString(dto));
            return true;
        } else {
            linksService.update(s, dtoToString(dto));
            log("No updates by link: " + s + ": " + dtoToString(dto));
            return false;
        }
    }

    protected Class<T> classMono;

    public AbstractClient(String baseUrl, LinksService linksService) {
        this.linksService = linksService;
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    protected abstract String transform(String link);

    protected abstract String dtoToString(T dto);

    public Mono<Boolean> fetch(String uri) {
        var link = transform(uri);
        return this.webClient.get().uri(link).retrieve().bodyToFlux(classMono).take(1)
            .map(t -> onReceipt(uri, t)).last().onErrorResume(t -> {
                onReceipt(uri, null);
                return Mono.just(true);
            });
    }

}
