package edu.java.bot.controllers;

import edu.java.dao.LinksDao;
import edu.java.utils.Utils;
import edu.java.utils.dto.ApiErrorResponse;
import edu.java.utils.dto.LinkUpdate;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UpdatesController {

    private final LinksDao linksDao;

    @ApiResponse(responseCode = "200", description = "Обновление обработано")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @PostMapping("/updates")
    public Mono<ResponseEntity<ApiErrorResponse>> update(@RequestBody LinkUpdate request) {
        return Mono.just(request).flatMap(linkUpdate -> {
            var updated = linksDao.updateLink(
                linkUpdate.getTgChatIds(),
                linkUpdate.getUrl().toString(),
                linkUpdate.getDescription()
            );
            if (updated) {
                return Mono.just(ResponseEntity.ok().build());
            } else {
                return Mono.just(Utils.errorRequest(HttpStatus.BAD_REQUEST.value()));
            }
        });

    }
}
