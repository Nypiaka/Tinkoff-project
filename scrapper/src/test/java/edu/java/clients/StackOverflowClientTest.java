package edu.java.clients;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.retry.BackOffPolicy;
import edu.java.retry.Restarter;
import edu.java.service.JdbcLinksService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@WireMockTest(httpPort = 8035)
public class StackOverflowClientTest {
    public static final String TEST_LOCALHOST_LINK = "http://localhost:8035/";

    private final JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);
    private final Restarter restarter = new Restarter(BackOffPolicy.LINEAR, 1, Duration.ZERO, List.of());
    StackOverflowClient stackOverflowClient = new StackOverflowClient(TEST_LOCALHOST_LINK, jdbcLinksService, restarter);

    @Test
    public void stackOverflowClientTest() {
        stubFor(get(urlEqualTo("/15250928/timeline?site=stackoverflow"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(Utils.STACKOVERFLOW_TEST_RESPONSE)));

        Mockito.when(jdbcLinksService.getLastUpdate(
                "stackoverflow.com/questions/15250928/how-to-change-springs-scheduled-fixeddelay-at-runtime"))
            .thenReturn("not updated");

        var result = new ArrayList<String>();

        Mockito.doAnswer(inv ->
                result.add(inv.getArgument(1))).when(jdbcLinksService)
            .update(
                Mockito.eq("stackoverflow.com/questions/15250928/how-to-change-springs-scheduled-fixeddelay-at-runtime"),
                Mockito.anyString()
            );

        stackOverflowClient.fetch(
            "stackoverflow.com/questions/15250928/how-to-change-springs-scheduled-fixeddelay-at-runtime").block();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(
            """
                New updates!
                Update time: 2023-12-21T00:00Z,
                Update type: vote_aggregate,
                Update actor: https://stackoverflow.com/users/256196/bohemian
                """, result.getFirst()
        );
    }
}
