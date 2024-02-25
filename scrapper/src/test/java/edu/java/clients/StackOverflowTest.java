package edu.java.clients;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.dao.LinksToUpdateDao;
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
public class StackOverflowTest {
    public static final String TEST_LOCALHOST_LINK = "http://localhost:8035/";

    private final LinksToUpdateDao linksToUpdateDao = Mockito.mock(LinksToUpdateDao.class);
    StackOverflowClient stackOverflowClient = new StackOverflowClient(TEST_LOCALHOST_LINK, linksToUpdateDao);

    @Test
    public void stackOverflowClientTest() {
        stubFor(get(urlEqualTo("/15250928/timeline?site=stackoverflow"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(Utils.STACKOVERFLOW_TEST_RESPONSE)));

        Mockito.when(linksToUpdateDao.getAllLinks()).thenReturn(
            Set.of("stackoverflow.com/questions/15250928/how-to-change-springs-scheduled-fixeddelay-at-runtime")
        );

        Mockito.when(linksToUpdateDao.get(
                "stackoverflow.com/questions/15250928/how-to-change-springs-scheduled-fixeddelay-at-runtime"))
            .thenReturn("not updated");

        var result = new ArrayList<String>();

        Mockito.doAnswer(inv ->
                result.add(inv.getArgument(1))).when(linksToUpdateDao)
            .save(
                Mockito.eq("stackoverflow.com/questions/15250928/how-to-change-springs-scheduled-fixeddelay-at-runtime"),
                Mockito.anyString()
            );

        stackOverflowClient.fetch(
            "stackoverflow.com/questions/15250928/how-to-change-springs-scheduled-fixeddelay-at-runtime");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(
            "StackOverflowUpdateDto(creationDate=2023-12-21T00:00Z, timelineType=vote_aggregate, postId=31109486, owner=OwnerDto(accountId=93689, userId=256196, link=https://stackoverflow.com/users/256196/bohemian))",
            result.getFirst()
        );
    }
}
