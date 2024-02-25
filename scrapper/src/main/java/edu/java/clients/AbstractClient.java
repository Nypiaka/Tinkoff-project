package edu.java.clients;

import org.springframework.web.reactive.function.client.WebClient;

public abstract class AbstractClient<T> {
    private final WebClient webClient;

    protected abstract void onReceipt(String s, T dto);

    protected Class<T> classMono;

    public AbstractClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    protected abstract String transform(String link);

    public void fetch(String uri) {
        var link = transform(uri);
        this.webClient.get().uri(link).retrieve().bodyToFlux(classMono).take(1)
            .subscribe(t -> onReceipt(uri, t));
    }

}
