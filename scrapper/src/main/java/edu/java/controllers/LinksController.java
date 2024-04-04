package edu.java.controllers;

import edu.java.service.LinksService;
import edu.java.utils.dto.AddLinkRequest;
import edu.java.utils.dto.ListLinksResponse;
import edu.java.utils.dto.RemoveLinkRequest;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinksController {
    private final LinksService linksService;
    private final Bucket bucket;

    @ApiResponse(responseCode = "200", description = "Ссылки успешно получены")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @GetMapping()
    public Mono<ListLinksResponse> getAllLinks(@RequestHeader("Tg-Chat-Id") Long id) {
        if (bucket.tryConsume(1)) {
            var result = linksService.getAllByChatId(id);
            return Mono.just(result);
        }
        throw new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS);
    }

    @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @PostMapping()
    public Mono<Void> addLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody AddLinkRequest request
    ) {
        if (bucket.tryConsume(1)) {
            if (linksService.saveLinkInChat(id, request.getLink().toString().toLowerCase())) {
                return Mono.empty();
            }
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
        }
        throw new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS);
    }

    @ApiResponse(responseCode = "200", description = "Ссылка успешно удалена")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @ApiResponse(responseCode = "404", description = "Ссылка не найдена")
    @DeleteMapping()
    public Mono<Void> removeLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody RemoveLinkRequest request
    ) {
        if (bucket.tryConsume(1)) {
            if (!linksService.containsChatAndLink(id, request.getLink().toString().toLowerCase())) {
                throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
            }
            if (linksService.removeLinkFromChat(id, request.getLink().toString().toLowerCase())) {
                return Mono.empty();
            }
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
        }
        throw new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS);
    }
}
