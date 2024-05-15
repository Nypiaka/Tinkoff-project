package edu.java.service;

import edu.java.utils.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class ScrapperQueueProducer {
    private final KafkaTemplate<Long, LinkUpdate> updateMessageKafkaTemplate;
    private final String topic;

    public void send(LinkUpdate update) {
        updateMessageKafkaTemplate.send(topic, update.getId(), update);
    }
}
