package edu.java.controllers;

import edu.java.dao.LinksDao;
import edu.java.scheduler.LinkUpdaterScheduler;
import edu.java.utils.Utils;
import edu.java.utils.dto.AddLinkRequest;
import edu.java.utils.dto.ApiErrorResponse;
import edu.java.utils.dto.LinkResponse;
import edu.java.utils.dto.ListLinksResponse;
import edu.java.utils.dto.RemoveLinkRequest;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinksController {
    private final LinksDao linksDao;

    @Autowired
    @VisibleForTesting
    private LinkUpdaterScheduler linkUpdaterScheduler;

    @ApiResponse(responseCode = "200", description = "Ссылки успешно получены")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @GetMapping()
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
                return Mono.just(Utils.errorRequest(HttpStatus.BAD_REQUEST.value()));
            }
        );
    }

    @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @PostMapping()
    public Mono<ResponseEntity<ApiErrorResponse>> addLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody AddLinkRequest request
    ) {
        return Mono.just(request).flatMap(
            req -> {
                if (linksDao.saveLink(id, req.getLink().toString(), "")) {
                    linkUpdaterScheduler.forceUpdate(req.getLink().toString());
                    return Mono.just(ResponseEntity.ok().build());
                }
                return Mono.just(Utils.errorRequest(HttpStatus.BAD_REQUEST.value()));
            }
        );
    }

    @ApiResponse(responseCode = "200", description = "Ссылка успешно удалена")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @ApiResponse(responseCode = "404", description = "Ссылка не найдена")
    @DeleteMapping()
    public Mono<ResponseEntity<ApiErrorResponse>> removeLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody RemoveLinkRequest request
    ) {
        return Mono.just(request).flatMap(
            req -> {
                if (!linksDao.containsLink(id, req.getLink().toString())) {
                    return Mono.just(Utils.errorRequest(HttpStatus.NOT_FOUND.value()));
                }
                if (linksDao.removeLink(id, req.getLink().toString())) {
                    return Mono.just(ResponseEntity.ok().build());
                }
                return Mono.just(Utils.errorRequest(HttpStatus.BAD_REQUEST.value()));
            }
        );
    }
}
