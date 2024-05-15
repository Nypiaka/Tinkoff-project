package edu.java.bot.configuration;

import edu.java.utils.dto.LinkUpdate;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@ConfigurationProperties(prefix = "app")
@ConfigurationPropertiesScan
public record KafkaConfiguration(
    @NotNull
    Kafka kafka) {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, LinkUpdate> linkUpdateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, LinkUpdate> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.addresses,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
        ), new LongDeserializer(), new JsonDeserializer<>(LinkUpdate.class).ignoreTypeHeaders()));
        return factory;
    }

    @Bean
    public KafkaTemplate<Long, LinkUpdate> defaultRetryTopicKafkaTemplate() {
        return new KafkaTemplate<>(
            new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.addresses,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
            )
            )
        );
    }

    public record Kafka(String addresses, String linkUpdateTopic, String linkUpdateGroup, String concurrency) {
    }

}
