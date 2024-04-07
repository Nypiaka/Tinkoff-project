package edu.java.bot.listeners;

import edu.java.bot.service.ChatsService;
import edu.java.utils.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConfigurationProperties
public class LinkUpdatesKafkaListener {
    private final ChatsService chatsService;
    private final KafkaTemplate<Long, LinkUpdate> defaultRetryTopicKafkaTemplate;

    @RetryableTopic
    @KafkaListener(topics = "#{@'app-edu.java.bot.configuration.KafkaConfiguration'.kafka.linkUpdateTopic}",
                   groupId = "#{@'app-edu.java.bot.configuration.KafkaConfiguration'.kafka.linkUpdateGroup}",
                   containerFactory = "linkUpdateKafkaListenerContainerFactory",
                   concurrency = "#{@'app-edu.java.bot.configuration.KafkaConfiguration'.kafka.concurrency}")
    public void listenLinkUpdates(
        @Payload LinkUpdate linkUpdate
    ) {
        chatsService.updateChatsInfo(linkUpdate);
    }

    @DltHandler
    public void listenDltLinkUpdates(LinkUpdate linkUpdate, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        defaultRetryTopicKafkaTemplate.send(topic.substring(0, topic.lastIndexOf("-dlt")) + "_dlq", linkUpdate);
    }
}
