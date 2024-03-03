package edu.java.bot.controllers;

import edu.java.bot.BotUtils;
import edu.java.bot.dao.LinksDao;
import edu.java.bot.dto.ApiErrorResponse;
import edu.java.bot.dto.LinkUpdate;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UpdatesController {

    private LinksDao linksDao;

    @ApiResponse(responseCode = "200", description = "Обновление обработано")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    public Mono<ResponseEntity<ApiErrorResponse>> update(@RequestBody LinkUpdate request) {
        return Mono.just(request).flatMap(linkUpdate -> {
            var updated = linksDao.updateLink(
                linkUpdate.getTgChatIds(),
                linkUpdate.getUrl().toString(),
                linkUpdate.getDescription()
            );
            if (updated) {
                return Mono.empty();
            } else {
                return Mono.just(BotUtils.errorRequest(400));
            }
        });

    }
}
