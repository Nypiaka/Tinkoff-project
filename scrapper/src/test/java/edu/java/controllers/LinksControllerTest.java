package edu.java.controllers;

import edu.java.Utils;
import edu.java.dao.LinksDao;
import edu.java.dto.handlers.AddLinkRequest;
import edu.java.dto.handlers.ApiErrorResponse;
import edu.java.dto.handlers.LinkResponse;
import edu.java.dto.handlers.ListLinksResponse;
import edu.java.dto.handlers.RemoveLinkRequest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class LinksControllerTest {

    @Test
    void testGetAllLinks_Success() {
        LinksDao linksDao = Mockito.mock(LinksDao.class);
        List<String> links = List.of("http://example.com", "http://example.org");
        when(linksDao.getList(anyLong())).thenReturn(links);

        LinksController controller = new LinksController(linksDao);

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
        LinksDao linksDao = Mockito.mock(LinksDao.class);
        when(linksDao.getList(anyLong())).thenReturn(List.of());

        LinksController controller = new LinksController(linksDao);

        Mono<ResponseEntity<?>> result = controller.getAllLinks(123L);

        ResponseEntity<?> expectedEntity = ResponseEntity.ok().body(new ListLinksResponse(List.of(), 0));
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testAddLink_Success() {
        LinksDao linksDao = Mockito.mock(LinksDao.class);
        when(linksDao.saveLink(anyLong(), any(), any())).thenReturn(true);

        LinksController controller = new LinksController(linksDao);

        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.addLink(123L, new AddLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = ResponseEntity.ok().build();
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testAddLink_Failure() {
        LinksDao linksDao = Mockito.mock(LinksDao.class);
        when(linksDao.saveLink(anyLong(), any(), any())).thenReturn(false);

        LinksController controller = new LinksController(linksDao);

        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.addLink(123L, new AddLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = Utils.errorRequest(HttpStatus.BAD_REQUEST.value());
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testRemoveLink_Success() {
        LinksDao linksDao = Mockito.mock(LinksDao.class);
        when(linksDao.containsLink(anyLong(), any())).thenReturn(true);
        when(linksDao.removeLink(anyLong(), any())).thenReturn(true);

        LinksController controller = new LinksController(linksDao);
        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.removeLink(123L, new RemoveLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = ResponseEntity.ok().build();
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testRemoveLink_NotFound() {
        LinksDao linksDao = Mockito.mock(LinksDao.class);
        when(linksDao.containsLink(anyLong(), any())).thenReturn(false);

        LinksController controller = new LinksController(linksDao);

        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.removeLink(123L, new RemoveLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = Utils.errorRequest(HttpStatus.NOT_FOUND.value());
        assertEquals(expectedEntity, result.block());
    }

    @Test
    void testRemoveLink_BadRequest() {
        LinksDao linksDao = Mockito.mock(LinksDao.class);
        when(linksDao.containsLink(anyLong(), any())).thenReturn(true);
        when(linksDao.removeLink(anyLong(), any())).thenReturn(false);

        LinksController controller = new LinksController(linksDao);

        Mono<ResponseEntity<ApiErrorResponse>> result =
            controller.removeLink(123L, new RemoveLinkRequest(URI.create("http://example.com")));

        ResponseEntity<ApiErrorResponse> expectedEntity = Utils.errorRequest(HttpStatus.BAD_REQUEST.value());
        assertEquals(expectedEntity, result.block());
    }
}
