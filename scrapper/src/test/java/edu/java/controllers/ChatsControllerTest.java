package edu.java.controllers;

import edu.java.service.JdbcLinksService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ChatsControllerTest {

    private static final long TEST_ID = 123L;

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void testRegisterChat(int ind) throws Exception {
        var params = List.of(Map.entry(true, HttpStatus.OK), Map.entry(false, HttpStatus.BAD_REQUEST));
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.registerChat(TEST_ID)).thenReturn(params.get(ind).getKey());
        ChatsController controller = new ChatsController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );
        if (ind == 0) {
            var result = controller.registerChat(TEST_ID);
            assertDoesNotThrow(() -> result.block());
        } else {
            var mockMvc =
                MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
            mockMvc.perform(MockMvcRequestBuilders.post("/chat-id/" + String.valueOf(TEST_ID)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.type").value("Bad Request"))
                .andExpect(jsonPath("$.exception-name").value("org.springframework.web.client.HttpServerErrorException"))
                .andExpect(jsonPath("$.exception-message").value("400 BAD_REQUEST"));
        }

    }

    @Test
    void testRemoveChatSuccess() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.removeChat(TEST_ID)).thenReturn(true);
        ChatsController controller = new ChatsController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );
        var result = controller.removeChat(TEST_ID);
        assertDoesNotThrow(() -> result.block());
    }

    @ParameterizedTest
    @ValueSource(
        ints = {0, 1}
    )
    void testRemoveChatProblems(int ind) throws Exception {
        var params = List.of(Map.entry(true, HttpStatus.BAD_REQUEST), Map.entry(false, HttpStatus.NOT_FOUND));
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.removeChat(TEST_ID)).thenReturn(false);
        when(jdbcLinksService.containsChat(TEST_ID)).thenReturn(params.get(ind).getKey());
        ChatsController controller = new ChatsController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );
        var mockMvc =
            MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/chat-id/" + String.valueOf(TEST_ID)))
            .andExpect(
                ind == 0 ? MockMvcResultMatchers.status().isBadRequest() : MockMvcResultMatchers.status().isNotFound())
            .andExpect(jsonPath("$.code").value(ind == 0 ? "400" : "404"))
            .andExpect(jsonPath("$.type").value(ind == 0 ? "Bad Request" : "Not Found"))
            .andExpect(jsonPath("$.exception-name").value("org.springframework.web.client.HttpServerErrorException"))
            .andExpect(jsonPath("$.exception-message").value(ind == 0 ? "400 BAD_REQUEST" : "404 NOT_FOUND"));
    }

}
