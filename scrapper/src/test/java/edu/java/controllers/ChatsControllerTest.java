package edu.java.controllers;

import edu.java.service.JdbcLinksService;
import edu.java.utils.dto.ApiErrorResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ChatsControllerTest {

    private static final long TEST_ID = 123L;

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void testRegisterChat(int ind) {
        var params = List.of(Map.entry(true, HttpStatus.OK), Map.entry(false, HttpStatus.BAD_REQUEST));
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.registerChat(TEST_ID)).thenReturn(params.get(ind).getKey());
        ChatsController controller = new ChatsController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );
        Mono<ResponseEntity<ApiErrorResponse>> result = controller.registerChat(TEST_ID);
        assertEquals(params.get(ind).getValue(), Objects.requireNonNull(result.block()).getStatusCode());
    }

    @Test
    void testRemoveChat_Success() {
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.removeChat(TEST_ID)).thenReturn(true);
        ChatsController controller = new ChatsController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );
        Mono<ResponseEntity<ApiErrorResponse>> result = controller.removeChat(TEST_ID);
        assertEquals(HttpStatus.OK, Objects.requireNonNull(result.block()).getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(
        ints = {0, 1}
    )
    void testRemoveChat_Porblems(int ind) {
        var params = List.of(Map.entry(true, HttpStatus.BAD_REQUEST), Map.entry(false, HttpStatus.NOT_FOUND));
        JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
        when(jdbcLinksService.removeChat(TEST_ID)).thenReturn(false);
        when(jdbcLinksService.containsChat(TEST_ID)).thenReturn(params.get(ind).getKey());
        ChatsController controller = new ChatsController(
            jdbcLinksService,
            Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofSeconds(10))).build()
        );
        Mono<ResponseEntity<ApiErrorResponse>> result = controller.removeChat(TEST_ID);
        assertEquals(params.get(ind).getValue(), result.block().getStatusCode());
    }

}
