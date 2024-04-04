package edu.java.controllers;

import edu.java.service.LinksService;
import edu.java.utils.dto.ApiErrorResponse;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Mono;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class ChatsController {
    private final LinksService linksService;
    private final Bucket bucket;

    @ApiResponse(responseCode = "200", description = "Чат зарегестрирован")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
        @Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApiErrorResponse.class)
        )})
    @PostMapping("/chat-id/{id}")
    public Mono<Void> registerChat(@PathVariable Long id) {
        if (bucket.tryConsume(1)) {
            var success = linksService.registerChat(id);
            if (success) {
                return Mono.empty();
            }
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
        }
        throw new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS);
    }

    @ApiResponse(responseCode = "200", description = "Чат успешно удалён")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @ApiResponse(responseCode = "404", description = "Чат не существует", content = {
        @Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApiErrorResponse.class)
        )})
    @DeleteMapping("/chat-id/{id}")
    public Mono<Void> removeChat(@PathVariable Long id) {
        if (bucket.tryConsume(1)) {
            var success = linksService.removeChat(id);
            if (success) {
                return Mono.empty();
            }
            var contains = linksService.containsChat(id);
            if (contains) {
                throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
            }
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
        }
        throw new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS);
    }

}
