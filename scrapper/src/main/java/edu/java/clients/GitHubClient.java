package edu.java.clients;

import edu.java.Utils;
import edu.java.dao.LinksToUpdateDao;
import edu.java.dto.github.GitHubUpdateDto;
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

    public GitHubClient(String baseUrl, LinksToUpdateDao dao) {
        super(baseUrl == null ? BASE_URL : baseUrl, dao);
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
