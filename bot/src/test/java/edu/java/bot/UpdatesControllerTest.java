package edu.java.bot;

import edu.java.bot.bot.LinksRefreshCheckerBot;
import edu.java.bot.controllers.UpdatesController;
import edu.java.bot.service.ChatsService;
import edu.java.utils.dto.LinkUpdate;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class UpdatesControllerTest {

    @Test
    void testUpdate_Success() {
        var chatsService = Mockito.mock(ChatsService.class);
        var visited = new AtomicBoolean(false);
        doAnswer(i -> {
            visited.set(true);
            return null;
        }).when(chatsService).updateChatsInfo(any());
        var linkUpdate = new LinkUpdate(123L, URI.create("http://example.com"), "description", List.of(1L));
        when(chatsService.updateChatsInfo(linkUpdate)).thenReturn(true);
        UpdatesController controller = new UpdatesController(
            chatsService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );

        Mono<ResponseEntity<?>> result =
            controller.update(linkUpdate);

        assertEquals(HttpStatus.OK, Objects.requireNonNull(result.block()).getStatusCode());
        assertTrue(visited.get());
    }

    @Test
    void testUpdate_Failure() {
        var bot = Mockito.mock(LinksRefreshCheckerBot.class);
        var chatsService = new ChatsService(bot);
        UpdatesController controller = new UpdatesController(
            chatsService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );
        doThrow(RuntimeException.class).when(bot).execute(any());
        Mono<ResponseEntity<?>> result =
            controller.update(new LinkUpdate(
                123L, URI.create("http://example.com"), "description", List.of(1L)));
        assertEquals(HttpStatus.BAD_REQUEST, Objects.requireNonNull(result.block()).getStatusCode());
    }
}
