package edu.java.scheduler;

import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.service.BotNotificationService;
import edu.java.service.LinksService;
import edu.java.utils.Utils;
import edu.java.utils.dto.LinkUpdate;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@ConfigurationProperties
public class LinkUpdaterScheduler {
    @Autowired
    private GitHubClient gitHubClient;

    @Autowired
    private StackOverflowClient stackOverflowClient;

    @Autowired
    private BotNotificationService botNotificationService;

    @Autowired
    private LinksService linksService;

    @Value("${app.scheduler.update-time}")
    private Integer updateTime;

    private void notifyBot(String link) {
        botNotificationService.update(new LinkUpdate(
            linksService.getId(link),
            URI.create(link),
            linksService.getLastUpdate(link),
            linksService.getChatsByLink(link)
        ));
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
    )
    public void update() {
        linksService.getAllLinksForInterval(updateTime).forEach(
            this::forceUpdate
        );
    }

}
