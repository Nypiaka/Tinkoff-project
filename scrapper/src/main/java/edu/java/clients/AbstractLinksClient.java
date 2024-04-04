package edu.java.clients;

import edu.java.retry.Restarter;
import edu.java.service.LinksService;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class AbstractLinksClient<T> {
    private final WebClient webClient;

    private final LinksService linksService;

    private final Restarter restarter;

    protected abstract Logger getLogger();

    private boolean shouldNotify(String s, T dto) {
        if (dto == null) {
            linksService.update(s, "Error in last update");
            return false;
        }
        var lastModified = linksService.getLastUpdate(s);
        if (lastModified == null || !lastModified.equals(dtoToString(dto))) {
            linksService.update(s, dtoToString(dto));
            getLogger().info("Updates by link: " + s + ": " + dtoToString(dto));
            return true;
        } else {
            linksService.update(s, dtoToString(dto));
            getLogger().info("No updates by link: " + s + ": " + dtoToString(dto));
            return false;
        }
    }

    protected Class<T> classMono;

    public AbstractLinksClient(String baseUrl, LinksService linksService, Restarter restarter) {
        this.linksService = linksService;
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.restarter = restarter;
    }

    protected abstract String transform(String link);

    protected abstract String dtoToString(T dto);

    public Mono<Boolean> fetch(String uri) {
        var link = transform(uri);
        return this.webClient.get().uri(link).retrieve().bodyToFlux(classMono).take(1)
            .map(t -> shouldNotify(uri, t)).last().retryWhen(restarter.getRetry())
            .onErrorResume(t -> {
                shouldNotify(uri, null);
                return Mono.just(true);
            });
    }

}
