package edu.java.bot;

import edu.java.bot.bot.LinksRefreshCheckerBot;
import edu.java.bot.controllers.UpdatesController;
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

public class UpdatesControllerTest {

    @Test
    void testUpdate_Success() {
        var linksRefreshCheckerBot = Mockito.mock(LinksRefreshCheckerBot.class);
        var visited = new AtomicBoolean(false);
        doAnswer(i -> {
            visited.set(true);
            return null;
        }).when(linksRefreshCheckerBot).execute(any());
        UpdatesController controller = new UpdatesController(linksRefreshCheckerBot);

        Mono<ResponseEntity<?>> result =
            controller.update(new LinkUpdate(123L, URI.create("http://example.com"), "description", List.of(1L)));

        assertEquals(HttpStatus.OK, result.block().getStatusCode());
        assertTrue(visited.get());
    }

    @Test
    void testUpdate_Failure() {
        var linksRefreshCheckerBot = Mockito.mock(LinksRefreshCheckerBot.class);
        UpdatesController controller = new UpdatesController(linksRefreshCheckerBot);
        doThrow(RuntimeException.class).when(linksRefreshCheckerBot).execute(any());
        Mono<ResponseEntity<?>> result =
            controller.update(new LinkUpdate(123L, URI.create("http://example.com"), "description", List.of(1L)));
        assertEquals(HttpStatus.BAD_REQUEST, result.block().getStatusCode());
    }
}
