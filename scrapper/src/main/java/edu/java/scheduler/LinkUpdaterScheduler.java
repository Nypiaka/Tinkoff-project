package edu.java.scheduler;

import edu.java.clients.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.service.LinksService;
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
    private LinksService linksService;

    @Autowired
    BotClient botClient;

    private void notifyBot(String link) {
        botClient.update(new LinkUpdate(
            linksService.getId(link),
            URI.create(link),
            linksService.getLastUpdate(link),
            linksService.getChatsByLink(link)
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
        linksService.getAllLinks("where c.updated_at <= now() at time zone 'MSK' - interval '5 minute'").forEach(
            this::forceUpdate
        );
    }

}
