package edu.java.scheduler;

import edu.java.Utils;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.dao.LinksDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class LinkUpdaterScheduler {

    @Autowired
    GitHubClient gitHubClient;

    @Autowired
    StackOverflowClient stackOverflowClient;

    @Autowired
    private LinksDao linksToUpdateDao;

    @Scheduled(
        fixedDelayString = "#{@'app-edu.java.configuration.ApplicationConfig'.scheduler.interval}"
    ) public void update() {
        linksToUpdateDao.getAllLinks().forEach(
            link -> {
                if (Utils.isGitHubLink(link)) {
                    gitHubClient.fetch(link);
                }
                if (Utils.isStackOverflowLink(link)) {
                    stackOverflowClient.fetch(link);
                }
            }
        );
    }

}
