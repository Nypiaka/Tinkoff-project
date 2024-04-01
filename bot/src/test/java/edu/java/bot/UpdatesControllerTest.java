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
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class UpdatesControllerTest {

    @Test
    void testUpdateSuccess() {
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

        var result =
            controller.update(linkUpdate);

        assertDoesNotThrow(() -> result.block());
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
        Exception er = null;
        try {
            controller.update(new LinkUpdate(
                123L, URI.create("http://example.com"), "description", List.of(1L))).block();
        } catch (Exception e) {
            er = e;
        }
        assertNotNull(er);
        assertInstanceOf(HttpServerErrorException.class, er);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ((HttpServerErrorException) er).getStatusCode());
    }
}
