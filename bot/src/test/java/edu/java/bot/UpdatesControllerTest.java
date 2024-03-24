package edu.java.bot;

import edu.java.bot.controllers.UpdatesController;
import edu.java.bot.service.ChatsService;
import edu.java.utils.dto.LinkUpdate;
import java.net.URI;
import java.util.List;
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
        UpdatesController controller = new UpdatesController(chatsService);

        Mono<ResponseEntity<?>> result =
            controller.update(linkUpdate);

        assertEquals(HttpStatus.OK, result.block().getStatusCode());
        assertTrue(visited.get());
    }

    @Test
    void testUpdate_Failure() {
        var chatsService = Mockito.mock(ChatsService.class);
        UpdatesController controller = new UpdatesController(chatsService);
        doThrow(RuntimeException.class).when(chatsService).updateChatsInfo(any());
        Mono<ResponseEntity<?>> result =
            controller.update(new LinkUpdate(123L, URI.create("http://example.com"), "description", List.of(1L)));
        assertEquals(HttpStatus.BAD_REQUEST, result.block().getStatusCode());
    }
}
