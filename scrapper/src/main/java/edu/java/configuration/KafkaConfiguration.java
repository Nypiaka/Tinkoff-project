package edu.java.configuration;

import edu.java.clients.BotClient;
import edu.java.service.BotNotificationService;
import edu.java.service.ScrapperQueueProducer;
import edu.java.utils.dto.LinkUpdate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@ConfigurationProperties(prefix = "app")
@RequiredArgsConstructor
public class KafkaConfiguration {
    private final BotClient botClient;
    private final ApplicationConfig applicationConfig;

    @Bean
    public ScrapperQueueProducer scrapperQueueProducer() {
        return new ScrapperQueueProducer(updateMessageKafkaTemplate(), applicationConfig.kafka().linkUpdateTopic());
    }

    @Bean
    public BotNotificationService botNotificationService() {
        return new BotNotificationService(
            botClient,
            scrapperQueueProducer(),
            applicationConfig.linkUpdateTransferType()
        );
    }

    @Bean
    public KafkaTemplate<Long, LinkUpdate> updateMessageKafkaTemplate() {
        return new KafkaTemplate<>(
            new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().addresses(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
            )
            )
        );
    }

}
