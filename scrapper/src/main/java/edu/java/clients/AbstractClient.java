package edu.java.clients;

import edu.java.dao.LinksToUpdateDao;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class AbstractClient<T> {
    private final WebClient webClient;

    private final LinksToUpdateDao dao;

    protected abstract void log(String line);

    private void onReceipt(String s, T dto) {
        var lastModified = dao.get(s);
        if (lastModified == null || !lastModified.equals(dtoToString(dto))) {
            dao.save(s, dtoToString(dto));
            log("Updates by link: " + s + ": " + dtoToString(dto));
        } else {
            log("No updates by link: " + s + ": " + dtoToString(dto));
        }
    }

    protected Class<T> classMono;

    public AbstractClient(String baseUrl, LinksToUpdateDao dao) {
        this.dao = dao;
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    protected abstract String transform(String link);

    protected abstract String dtoToString(T dto);

    public void fetch(String uri) {
        var link = transform(uri);
        this.webClient.get().uri(link).retrieve().bodyToFlux(classMono).take(1)
            .subscribe(t -> onReceipt(uri, t));
    }

}
