package edu.java.controllers;

import edu.java.Utils;
import edu.java.dao.LinksDao;
import edu.java.dto.handlers.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class ChatsController {
    private final LinksDao linksDao;

    @ApiResponse(responseCode = "200", description = "Чат зарегестрирован")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
        @Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApiErrorResponse.class)
        )})
    @PostMapping("/chat-id/{id}")
    public Mono<ResponseEntity<ApiErrorResponse>> registerChat(@PathVariable Long id) {
        var success = linksDao.registerChat(id);
        if (success) {
            return Mono.just(ResponseEntity.ok().build());
        }
        return Mono.just(Utils.errorRequest(HttpStatus.BAD_REQUEST.value()));
    }

    @ApiResponse(responseCode = "200", description = "Чат успешно удалён")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @ApiResponse(responseCode = "404", description = "Чат не существует", content = {
        @Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ApiErrorResponse.class)
        )})
    @DeleteMapping("/chat-id/{id}")
    public Mono<ResponseEntity<ApiErrorResponse>> removeChat(@PathVariable Long id) {
        var success = linksDao.removeChat(id);
        if (success) {
            return Mono.just(ResponseEntity.ok().build());
        }
        var contains = linksDao.containsChat(id);
        return Mono.just(contains).flatMap(
            cont -> {
                if (cont) {
                    return Mono.just(Utils.errorRequest(HttpStatus.BAD_REQUEST.value()));
                }
                return Mono.just(Utils.errorRequest(HttpStatus.NOT_FOUND.value()));
            }
        );
    }

}
