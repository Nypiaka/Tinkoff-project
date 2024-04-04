package edu.java.clients;

import edu.java.dto.github.GitHubUpdateDto;
import edu.java.retry.Restarter;
import edu.java.service.LinksService;
import edu.java.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GitHubClient extends AbstractLinksClient<GitHubUpdateDto> {
    private final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    private static final String BASE_URL = "https://api.github.com/repos/";

    @Override
    protected Logger getLogger() {
        return logger;
    }

    public GitHubClient(String baseUrl, LinksService linksService, Restarter restarter) {
        super(baseUrl == null ? BASE_URL : baseUrl, linksService, restarter);
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
