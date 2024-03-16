package edu.java.clients;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.dao.JdbcLinksDao;
import java.util.ArrayList;
import java.util.Set;
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

    private final JdbcLinksDao jdbcLinksDao = Mockito.mock(JdbcLinksDao.class);
    GitHubClient gitHubClient = new GitHubClient(TEST_LOCALHOST_LINK, jdbcLinksDao);

    @Test
    public void githubClientTest() {
        stubFor(get(urlEqualTo("/nypiaka/itmo-projects/activity"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(Utils.GITHUB_TEST_RESPONSE)));

        Mockito.when(jdbcLinksDao.getAllLinks("where c.updated_at <= now() at time zone 'MSK' - interval '5 minute'"))
            .thenReturn(
                Set.of("github.com/nypiaka/itmo-projects")
            );

        Mockito.when(jdbcLinksDao.getLastUpdate("github.com/nypiaka/itmo-projects")).thenReturn("not updated");

        var result = new ArrayList<String>();

        Mockito.doAnswer(inv ->
                result.add(inv.getArgument(1))).when(jdbcLinksDao)
            .save(Mockito.eq("github.com/nypiaka/itmo-projects"), Mockito.anyString());

        gitHubClient.fetch("github.com/nypiaka/itmo-projects").block();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(
            "GitHubUpdateDto(id=16822682753, before=18234e40d98ea4e27561b6b4ebd14590fc330c39, after=604c5d02a880f690ba4e9221027721e42c7db2cd, timeStamp=2024-01-27T19:09Z, activityType=push, actor=UserDto(login=Nypiaka))",
            result.getFirst()
        );
    }
}
