package edu.java.scheduler;

import edu.java.clients.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.dao.LinksDao;
import edu.java.utils.Utils;
import edu.java.utils.dto.LinkUpdate;
import java.net.URI;
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
    private LinksDao linksDao;

    @Autowired
    BotClient botClient;

    private void notifyBot(String link) {
        botClient.update(new LinkUpdate(
            linksDao.getId(link), URI.create(link), linksDao.getLastUpdate(link), linksDao.getChatsByLink(link)
        )).subscribe();
    }

    public void forceUpdate(String link) {
        if (Utils.isGitHubLink(link)) {
            gitHubClient.fetch(link).subscribe(res -> {
                if (res) {
                    notifyBot(link);
                }
            });
        }

        if (Utils.isStackOverflowLink(link)) {
            stackOverflowClient.fetch(link).subscribe(res -> {
                if (res) {
                    notifyBot(link);
                }
            });
        }
    }

    @Scheduled(
        fixedDelayString = "#{@'app-edu.java.configuration.ApplicationConfig'.scheduler.interval}"
    ) public void update() {
        linksDao.getAllLinks("where c.updated_at <= now() at time zone 'MSK' - interval '5 minute'").forEach(
            this::forceUpdate
        );
    }

}
