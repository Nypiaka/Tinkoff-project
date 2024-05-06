package edu.java.service;

import edu.java.clients.BotClient;
import edu.java.utils.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BotNotificationService {

    private final BotClient botClient;

    private final ScrapperQueueProducer scrapperQueueProducer;

    private final TransferType transferType;

    public void update(LinkUpdate linkUpdate) {
        if (transferType == TransferType.KAFKA) {
            scrapperQueueProducer.send(linkUpdate);
        } else {
            botClient.update(linkUpdate).subscribe();
        }
    }
}
