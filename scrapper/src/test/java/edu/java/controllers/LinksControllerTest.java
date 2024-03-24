package edu.java.controllers;

import edu.java.scheduler.LinkUpdaterScheduler;
import edu.java.service.JdbcLinksService;
import edu.java.utils.Utils;
import edu.java.utils.dto.AddLinkRequest;
import edu.java.utils.dto.ApiErrorResponse;
import edu.java.utils.dto.LinkResponse;
import edu.java.utils.dto.ListLinksResponse;
import edu.java.utils.dto.RemoveLinkRequest;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class LinksControllerTest {

    @Test
    void testGetAllLinks_Success() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.getAllByChatId(anyLong())).thenReturn(new ListLinksResponse(
            List.of(
                new LinkResponse(0L, URI.create("http://example.com")),
                new LinkResponse(1L, URI.create("http://example.org"))
            ),
            2
        ));

        LinksController controller = new LinksController(jdbcLinksService);

        Mono<ResponseEntity<?>> result = controller.getAllLinks(123L);

        ListLinksResponse expectedResponse = new ListLinksResponse(
            List.of(
                new LinkResponse(0L, URI.create("http://example.com")),
                new LinkResponse(1L, URI.create("http://example.org"))
            ),
            2
        );
        ResponseEntity<?> expectedEntity = ResponseEntity.ok().body(expectedResponse);
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testGetAllLinks_Empty() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.getAllByChatId(anyLong())).thenReturn(new ListLinksResponse(List.of(), 0));

        LinksController controller = new LinksController(jdbcLinksService);

        Mono<ResponseEntity<?>> result = controller.getAllLinks(123L);

        ResponseEntity<?> expectedEntity = ResponseEntity.ok().body(new ListLinksResponse(List.of(), 0));
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testAddLink_Success() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);

        var forceUpdated = new AtomicBoolean(false);

        doAnswer(s -> {
            forceUpdated.set(true);
            return null;
        }).when(jdbcLinksService).saveLinkAndUpdate(anyLong(), any(), any());

        when(jdbcLinksService.saveLinkAndUpdate(anyLong(), any(), any())).thenReturn(true);

        LinksController controller = new LinksController(jdbcLinksService);

        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.addLink(123L, new AddLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = ResponseEntity.ok().build();
        assertEquals(expectedEntity, result.block());
        assertTrue(forceUpdated.get());
    }

    @Test
    void testAddLink_Failure() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.saveLinkAndUpdate(anyLong(), any(), any())).thenReturn(false);

        LinksController controller = new LinksController(jdbcLinksService);

        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.addLink(123L, new AddLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = Utils.errorRequest(HttpStatus.BAD_REQUEST.value());
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testRemoveLink_Success() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.containsLink(anyLong(), any())).thenReturn(true);
        when(jdbcLinksService.removeLink(anyLong(), any())).thenReturn(true);

        LinksController controller = new LinksController(jdbcLinksService);
        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.removeLink(123L, new RemoveLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = ResponseEntity.ok().build();
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testRemoveLink_NotFound() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.containsLink(anyLong(), any())).thenReturn(false);

        LinksController controller = new LinksController(jdbcLinksService);

        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.removeLink(123L, new RemoveLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = Utils.errorRequest(HttpStatus.NOT_FOUND.value());
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testRemoveLink_BadRequest() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.containsLink(anyLong(), any())).thenReturn(true);
        when(jdbcLinksService.removeLink(anyLong(), any())).thenReturn(false);

        LinksController controller = new LinksController(jdbcLinksService);

        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.removeLink(123L, new RemoveLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = Utils.errorRequest(HttpStatus.BAD_REQUEST.value());
        assertEquals(expectedEntity, result.block());
    }
}
