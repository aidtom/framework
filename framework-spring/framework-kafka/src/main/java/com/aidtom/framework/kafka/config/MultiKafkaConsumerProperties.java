package com.aidtom.framework.kafka.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * kafka
 *
 * @author tom
 * @date 2020/11/25 17:35
 */
@ConfigurationProperties(prefix = "framework.kafka.consumer")
@EqualsAndHashCode(callSuper = true)
@Data
public class MultiKafkaConsumerProperties extends KafkaConsumerProperties {


    private Map<String, KafkaConsumerProperties> configuration;

    public Consumer buildConsumer(String name) {
        KafkaConsumerProperties targetConsumerProperties = configuration.get(name);
        if (targetConsumerProperties == null) {
            throw new IllegalArgumentException("can't found KafkaConsumer properties. name=" + name);
        }

        KafkaConfig consumerConfig = new KafkaConfig();

        Stream.of(targetConsumerProperties, this).forEach(consumerProperties -> {

            Optional.ofNullable(consumerProperties.getConfig()).ifPresent(consumerConfig::putAllAbsent);

            consumerConfig.computeIfAbsent(ConsumerConfig.CLIENT_ID_CONFIG, consumerProperties::getClientId);
            consumerConfig.computeIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties::getGroupId);
            consumerConfig.computeIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties::getBootstrapServers);
            consumerConfig.computeIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerProperties::getKeyDeserializer);
            consumerConfig.computeIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerProperties::getValueDeserializer);
            consumerConfig.computeIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerProperties::getEnableAutoCommit);
            consumerConfig.computeIfAbsent(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, consumerProperties::getAutoCommitIntervalMs);
            consumerConfig.computeIfAbsent(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, consumerProperties::getFetchMinBytes);
            consumerConfig.computeIfAbsent(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, consumerProperties::getFetchMaxBytes);

        });

        KafkaConsumer consumer = new KafkaConsumer(consumerConfig);

        consumer.subscribe(targetConsumerProperties.getTopics());

        return consumer;
    }


}
