package edu.java.clients;

import edu.java.dto.stackoverflow.StackOverflowUpdatesDto;
import edu.java.retry.Restarter;
import edu.java.service.LinksService;
import edu.java.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StackOverflowClient extends AbstractClient<StackOverflowUpdatesDto> {

    private final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    private static final String BASE_URL = "https://api.stackexchange.com/2.3/questions/";

    @Override
    protected void log(String line) {
        logger.info(line);
    }

    public StackOverflowClient(String baseUrl, LinksService linksService, Restarter restarter) {
        super(baseUrl == null ? BASE_URL : baseUrl, linksService, restarter);
        this.classMono = StackOverflowUpdatesDto.class;
    }

    @Override
    protected String transform(String link) {
        return Utils.stackOverflowLinkToUri(link);
    }

    @Override
    protected String dtoToString(StackOverflowUpdatesDto dto) {
        if (dto.getItems() == null || dto.getItems().getFirst() == null) {
            return null;
        }
        return dto.getItems().getFirst().toString();
    }

}
