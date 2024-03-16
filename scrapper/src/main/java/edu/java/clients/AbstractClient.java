package edu.java.clients;

import edu.java.dao.LinksDao;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class AbstractClient<T> {
    private final WebClient webClient;

    private final LinksDao dao;

    protected abstract void log(String line);

    private boolean onReceipt(String s, T dto) {
        var lastModified = dao.getLastUpdate(s);
        if (lastModified == null || !lastModified.equals(dtoToString(dto))) {
            dao.save(s, dtoToString(dto));
            log("Updates by link: " + s + ": " + dtoToString(dto));
            return true;
        } else {
            dao.save(s, dtoToString(dto));
            log("No updates by link: " + s + ": " + dtoToString(dto));
            return false;
        }
    }

    protected Class<T> classMono;

    public AbstractClient(String baseUrl, LinksDao dao) {
        this.dao = dao;
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    protected abstract String transform(String link);

    protected abstract String dtoToString(T dto);

    public Mono<Boolean> fetch(String uri) {
        var link = transform(uri);
        return this.webClient.get().uri(link).retrieve().bodyToFlux(classMono).take(1)
            .map(t -> onReceipt(uri, t)).last();
    }

}
