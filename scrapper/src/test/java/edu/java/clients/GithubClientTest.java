package edu.java.clients;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.retry.BackOffPolicy;
import edu.java.retry.Restarter;
import edu.java.service.JdbcLinksService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@WireMockTest(httpPort = 8035)
public class GithubClientTest {
    public static final String TEST_LOCALHOST_LINK = "http://localhost:8035/";

    private final JdbcLinksService jdbcLinksService = Mockito.mock(JdbcLinksService.class);

    private final Restarter restarter = new Restarter(BackOffPolicy.LINEAR, 1, Duration.ZERO, List.of());
    GitHubClient gitHubClient = new GitHubClient(TEST_LOCALHOST_LINK, jdbcLinksService, restarter);

    @Test
    @SneakyThrows
    public void githubClientTest() {
        stubFor(get(urlEqualTo("/nypiaka/itmo-projects/activity"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(Utils.GITHUB_TEST_RESPONSE)));

        Mockito.when(jdbcLinksService.getLastUpdate("github.com/nypiaka/itmo-projects")).thenReturn("not updated");

        var result = new ArrayList<String>();

        Mockito.doAnswer(inv ->
                result.add(inv.getArgument(1))).when(jdbcLinksService)
            .update(Mockito.eq("github.com/nypiaka/itmo-projects"), Mockito.anyString());

        gitHubClient.fetch("github.com/nypiaka/itmo-projects").block();
        Thread.sleep(1000);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(
            """
                New updates!
                Update time: 2024-01-27T19:09Z,
                Update type: push,
                Update actor: Nypiaka
                """, result.getFirst()
        );
    }
}
