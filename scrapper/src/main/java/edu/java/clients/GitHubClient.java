package edu.java.clients;

import edu.java.dto.github.GitHubUpdateDto;
import edu.java.service.LinksService;
import edu.java.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GitHubClient extends AbstractClient<GitHubUpdateDto> {
    private final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    private static final String BASE_URL = "https://api.github.com/repos/";

    @Override
    protected void log(String line) {
        logger.info(line);
    }

    public GitHubClient(String baseUrl, LinksService linksService) {
        super(baseUrl == null ? BASE_URL : baseUrl, linksService);
        this.classMono = GitHubUpdateDto.class;
    }

    @Override
    protected String transform(String link) {
        return Utils.githubLinkToUri(link);
    }

    @Override
    protected String dtoToString(GitHubUpdateDto dto) {
        return dto.toString();
    }
}
