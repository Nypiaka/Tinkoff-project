package edu.java.bot.controllers;

import edu.java.bot.service.ChatsService;
import edu.java.utils.Utils;
import edu.java.utils.dto.ApiErrorResponse;
import edu.java.utils.dto.LinkUpdate;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdatesController {

    private final ChatsService chatsService;
    private final Bucket bucket;

    @ApiResponse(responseCode = "200", description = "Обновление обработано")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @PostMapping()
    public Mono<Void> update(@RequestBody LinkUpdate request) {
        if (bucket.tryConsume(1)) {
            if (chatsService.updateChatsInfo(request)) {
                return Mono.empty();
            }
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
        }
        throw new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        if (ex instanceof HttpServerErrorException casted) {
            return ResponseEntity.status(casted.getStatusCode())
                .body(Utils.errorRequest(casted.getStatusCode().value(), casted));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Utils.errorRequest(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex));
    }
}
