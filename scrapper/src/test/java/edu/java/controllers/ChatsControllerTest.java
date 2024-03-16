package edu.java.controllers;

import edu.java.dao.JdbcLinksDao;
import java.util.Objects;
import edu.java.utils.dto.ApiErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ChatsControllerTest {

    private static final long TEST_ID = 123L;

    @Test
    void testRegisterChat_Success() {
        JdbcLinksDao jdbcLinksDao = Mockito.mock(JdbcLinksDao.class);
        when(jdbcLinksDao.registerChat(TEST_ID)).thenReturn(true);

        ChatsController controller = new ChatsController(jdbcLinksDao);

        Mono<ResponseEntity<ApiErrorResponse>> result = controller.registerChat(TEST_ID);
        assertEquals(HttpStatus.OK, Objects.requireNonNull(result.block()).getStatusCode());
    }

    @Test
    void testRegisterChat_Failure() {
        JdbcLinksDao jdbcLinksDao = Mockito.mock(JdbcLinksDao.class);
        when(jdbcLinksDao.registerChat(TEST_ID)).thenReturn(false);
        ChatsController controller = new ChatsController(jdbcLinksDao);
        Mono<ResponseEntity<ApiErrorResponse>> result = controller.registerChat(TEST_ID);
        assertEquals(HttpStatus.BAD_REQUEST, Objects.requireNonNull(result.block()).getStatusCode());
    }

    @Test
    void testRemoveChat_Success() {
        JdbcLinksDao jdbcLinksDao = Mockito.mock(JdbcLinksDao.class);
        when(jdbcLinksDao.removeChat(TEST_ID)).thenReturn(true);
        ChatsController controller = new ChatsController(jdbcLinksDao);
        Mono<ResponseEntity<ApiErrorResponse>> result = controller.removeChat(TEST_ID);
        assertEquals(HttpStatus.OK, result.block().getStatusCode());
    }

    @Test
    void testRemoveChat_NotFound() {
        JdbcLinksDao jdbcLinksDao = Mockito.mock(JdbcLinksDao.class);
        when(jdbcLinksDao.removeChat(TEST_ID)).thenReturn(false);
        when(jdbcLinksDao.containsChat(TEST_ID)).thenReturn(false);
        ChatsController controller = new ChatsController(jdbcLinksDao);
        Mono<ResponseEntity<ApiErrorResponse>> result = controller.removeChat(TEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, result.block().getStatusCode());
    }

    @Test
    void testRemoveChat_BadRequest() {
        JdbcLinksDao jdbcLinksDao = Mockito.mock(JdbcLinksDao.class);
        when(jdbcLinksDao.removeChat(TEST_ID)).thenReturn(false);
        when(jdbcLinksDao.containsChat(TEST_ID)).thenReturn(true);
        ChatsController controller = new ChatsController(jdbcLinksDao);
        Mono<ResponseEntity<ApiErrorResponse>> result = controller.removeChat(TEST_ID);
        assertEquals(HttpStatus.BAD_REQUEST, result.block().getStatusCode());
    }
}
