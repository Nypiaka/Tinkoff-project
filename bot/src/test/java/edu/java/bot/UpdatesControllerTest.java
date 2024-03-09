package edu.java.bot;

import edu.java.bot.controllers.UpdatesController;
import edu.java.bot.dao.LinksDao;
import java.net.URI;
import java.util.List;
import edu.java.utils.dto.ApiErrorResponse;
import edu.java.utils.dto.LinkUpdate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UpdatesControllerTest {

    @Test
    void testUpdate_Success() {
        LinksDao linksDao = Mockito.mock(LinksDao.class);
        when(linksDao.updateLink(any(), any(), any())).thenReturn(true);
        UpdatesController controller = new UpdatesController(linksDao);

        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.update(new LinkUpdate(123L, URI.create("http://example.com"), "description", List.of()));

        assertEquals(HttpStatus.OK, result.block().getStatusCode());
    }

    @Test
    void testUpdate_Failure() {

        LinksDao linksDao = Mockito.mock(LinksDao.class);
        when(linksDao.updateLink(any(), any(), any())).thenReturn(false);
        UpdatesController controller = new UpdatesController(linksDao);
        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.update(new LinkUpdate(123L, URI.create("http://example.com"), "description", List.of()));
        assertEquals(HttpStatus.BAD_REQUEST, result.block().getStatusCode());
    }
}
