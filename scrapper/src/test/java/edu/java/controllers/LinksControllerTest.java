package edu.java.controllers;

import edu.java.service.JdbcLinksService;
import edu.java.utils.dto.AddLinkRequest;
import edu.java.utils.dto.LinkResponse;
import edu.java.utils.dto.ListLinksResponse;
import edu.java.utils.dto.RemoveLinkRequest;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class LinksControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
        MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        StandardCharsets.UTF_8
    );

    static String objectToJson(Object val) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(val);
    }

    @Test
    void testGetAllLinksSuccess() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.getAllByChatId(anyLong())).thenReturn(new ListLinksResponse(
            List.of(
                new LinkResponse(0L, URI.create("http://example.com")),
                new LinkResponse(1L, URI.create("http://example.org"))
            ),
            2
        ));

        LinksController controller = new LinksController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );

        var result = controller.getAllLinks(123L);

        ListLinksResponse expectedResponse = new ListLinksResponse(
            List.of(
                new LinkResponse(0L, URI.create("http://example.com")),
                new LinkResponse(1L, URI.create("http://example.org"))
            ),
            2
        );
        assertDoesNotThrow(() -> result.block());
        assertEquals(expectedResponse, result.block());
    }

    @Test
    void testGetAllLinksEmpty() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.getAllByChatId(anyLong())).thenReturn(new ListLinksResponse(List.of(), 0));

        LinksController controller = new LinksController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );

        var result = controller.getAllLinks(123L);
        assertDoesNotThrow(() -> result.block());
        assertEquals(new ListLinksResponse(List.of(), 0), result.block());
    }

    @Test
    void testAddLinkSuccess() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);

        var forceUpdated = new AtomicBoolean(false);

        doAnswer(s -> {
            forceUpdated.set(true);
            return null;
        }).when(jdbcLinksService).saveLinkInChat(anyLong(), any());

        when(jdbcLinksService.saveLinkInChat(anyLong(), any())).thenReturn(true);

        LinksController controller = new LinksController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );

        var result = controller.addLink(123L, new AddLinkRequest(URI.create("http://example.com")));
        assertDoesNotThrow(() -> result.block());
        assertTrue(forceUpdated.get());
    }

    @Test
    void testAddLinkFailure() throws Exception {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.saveLinkInChat(anyLong(), any())).thenReturn(false);

        LinksController controller = new LinksController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );

        var mockMvc =
            MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        var json = objectToJson(new AddLinkRequest(URI.create("http://example.com")));
        mockMvc.perform(MockMvcRequestBuilders.post("/links").header("Tg-Chat-Id", 123L)
                .contentType(APPLICATION_JSON_UTF8).content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.type").value("Bad Request"))
            .andExpect(jsonPath("$.exception-name").value("org.springframework.web.client.HttpServerErrorException"))
            .andExpect(jsonPath("$.exception-message").value("400 BAD_REQUEST"));
    }

    @Test
    void testRemoveLinkSuccess() throws JsonProcessingException {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.removeLinkFromChat(anyLong(), any())).thenReturn(true);
        when(jdbcLinksService.containsChatAndLink(anyLong(), any())).thenReturn(true);

        LinksController controller = new LinksController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );
        assertDoesNotThrow(() -> controller.removeLink(123L, new RemoveLinkRequest(URI.create("http://example.com")))
            .block());
    }

    @Test
    void testRemoveLinkNotFound() throws Exception {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.containsChatAndLink(anyLong(), any())).thenReturn(false);

        LinksController controller = new LinksController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );

        var mockMvc =
            MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        var json = objectToJson(new RemoveLinkRequest(URI.create("http://example.com")));

        mockMvc.perform(MockMvcRequestBuilders.delete("/links").header("Tg-Chat-Id", 123L)
                .contentType(APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(jsonPath("$.code").value("404"))
            .andExpect(jsonPath("$.type").value("Not Found"))
            .andExpect(jsonPath("$.exception-name").value("org.springframework.web.client.HttpServerErrorException"))
            .andExpect(jsonPath("$.exception-message").value("404 NOT_FOUND"));
    }

    @Test
    void testRemoveLinkBadRequest() throws Exception {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.containsChatAndLink(anyLong(), any())).thenReturn(true);
        when(jdbcLinksService.removeLinkFromChat(anyLong(), any())).thenReturn(false);

        LinksController controller = new LinksController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );

        var mockMvc =
            MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        var json = objectToJson(new RemoveLinkRequest(URI.create("http://example.com")));

        mockMvc.perform(MockMvcRequestBuilders.delete("/links").header("Tg-Chat-Id", 123L)
                .contentType(APPLICATION_JSON_UTF8).content(json)).andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.type").value("Bad Request"))
            .andExpect(jsonPath("$.exception-name").value("org.springframework.web.client.HttpServerErrorException"))
            .andExpect(jsonPath("$.exception-message").value("400 BAD_REQUEST"));
    }
}
