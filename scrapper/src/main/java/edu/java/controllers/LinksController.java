package edu.java.controllers;

import edu.java.Utils;
import edu.java.dao.LinksDao;
import edu.java.dto.handlers.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinksController {
    private final LinksDao linksDao;

    @ApiResponse(responseCode = "200", description = "Ссылки успешно получены")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @GetMapping
    public Mono<ResponseEntity<?>> getAllLinks(@RequestHeader("Tg-Chat-Id") Long id) {
        var links = linksDao.getList(id);
        return Mono.just(links).flatMap(
            l -> {
                if (l != null) {
                    AtomicLong counter = new AtomicLong(0L);
                    var result = new ListLinksResponse(links.stream().map(
                        link -> new LinkResponse(counter.getAndIncrement(), URI.create(link))
                    ).toList(), links.size());
                    return Mono.just(ResponseEntity.ok().body(result));
                }
                return Mono.just(Utils.errorRequest(400));
            }
        );
    }

    @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @PostMapping(

    )
    public Mono<ResponseEntity<ApiErrorResponse>> addLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody AddLinkRequest request
    ) {
        return Mono.just(request).flatMap(
            req -> {
                if (linksDao.saveLink(id, req.getLink().toString(), "")) {
                    return Mono.empty();
                }
                return Mono.just(Utils.errorRequest(400));
            }
        );
    }

    @ApiResponse(responseCode = "200", description = "Ссылка успешно удалена")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @ApiResponse(responseCode = "404", description = "Ссылка не найдена")
    @DeleteMapping
    public Mono<ResponseEntity<ApiErrorResponse>> removeLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody RemoveLinkRequest request
    ) {
        return Mono.just(request).flatMap(
            req -> {
                if (!linksDao.containsLink(id, req.getLink().toString())) {
                    return Mono.just(Utils.errorRequest(404));
                }
                if (linksDao.removeLink(id, req.getLink().toString())) {
                    return Mono.empty();
                }
                return Mono.just(Utils.errorRequest(400));
            }
        );
    }
}
