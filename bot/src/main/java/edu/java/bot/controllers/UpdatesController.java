package edu.java.bot.controllers;

import edu.java.bot.service.ChatsService;
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

    private final ChatsService chatsService;

    @ApiResponse(responseCode = "200", description = "Обновление обработано")
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    @PostMapping()
    public Mono<ResponseEntity<?>> update(@RequestBody LinkUpdate request) {
        if (chatsService.updateChatsInfo(request)) {
            return Mono.just(ResponseEntity.ok().build());
        }
        return Mono.just(Utils.errorRequest(HttpStatus.BAD_REQUEST.value()));
    }
}
