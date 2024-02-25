package edu.java.clients;

import edu.java.Utils;
import edu.java.dao.LinksToUpdateDao;
import edu.java.dto.github.GitHubUpdateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GitHubClient extends AbstractClient<GitHubUpdateDto> {
    private final LinksToUpdateDao linksToUpdateDao;
    private final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    private static final String BASE_URL = "https://api.github.com/repos/";

    @Override
    protected void onReceipt(String s, GitHubUpdateDto dto) {
        var lastModified = linksToUpdateDao.get(s);
        if (lastModified == null || !lastModified.equals(dto.toString())) {
            linksToUpdateDao.save(s, dto.toString());
            logger.info("Updates by link: " + s + ": " + dto);
        } else {
            logger.info("No updates by link: " + s + ": " + dto);
        }
    }

    public GitHubClient(String baseUrl, LinksToUpdateDao dao) {
        super(baseUrl == null ? BASE_URL : baseUrl);
        this.linksToUpdateDao = dao;
        this.classMono = GitHubUpdateDto.class;
    }

    @Override
    protected String transform(String link) {
        return Utils.githubLinkToUri(link);
    }
}
