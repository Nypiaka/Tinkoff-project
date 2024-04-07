package edu.java.bot.listeners;

import edu.java.bot.service.ChatsService;
import edu.java.utils.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConfigurationProperties
public class LinkUpdatesKafkaListener {
    private final ChatsService chatsService;

    @KafkaListener(topics = "#{@'app-edu.java.bot.configuration.KafkaConfiguration'.kafka.linkUpdateTopic}",
                   groupId = "#{@'app-edu.java.bot.configuration.KafkaConfiguration'.kafka.linkUpdateGroup}",
                   containerFactory = "linkUpdateKafkaListenerContainerFactory",
                   concurrency = "#{@'app-edu.java.bot.configuration.KafkaConfiguration'.kafka.concurrency}")
    public void listenLinkUpdates(
        @Payload LinkUpdate linkUpdate
    ) {
        chatsService.updateChatsInfo(linkUpdate);
    }

}
