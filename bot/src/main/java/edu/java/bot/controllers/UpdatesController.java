package edu.java.bot.controllers;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.LinksRefreshCheckerBot;
import edu.java.utils.Utils;
import edu.java.utils.dto.LinkUpdate;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdatesController {
    private final LinksRefreshCheckerBot linksRefreshCheckerBot;

    @ApiResponse(responseCode = "200", description = "Обновление обработано")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @PostMapping()
    public Mono<ResponseEntity<?>> update(@RequestBody LinkUpdate request) {
        return Mono.just(request).flatMap(req -> {
            try {
                for (var id : request.getTgChatIds()) {
                    linksRefreshCheckerBot.execute(new SendMessage(
                        id, "Updates by link: " + req.getUrl() + "\n" + req.getDescription()
                    ));
                }
                return Mono.just(ResponseEntity.ok().build());
            } catch (Exception ignored) {
                return Mono.just(Utils.errorRequest(HttpStatus.BAD_REQUEST.value()));
            }
        });
    }
}
