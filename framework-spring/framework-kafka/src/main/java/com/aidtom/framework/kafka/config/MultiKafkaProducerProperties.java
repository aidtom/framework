package com.aidtom.framework.kafka.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * kafka
 *
 * @author tanghaihua
 * @date 2020/11/25 17:35
 */
@ConfigurationProperties(prefix = "framework.kafka.producer")
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class MultiKafkaProducerProperties extends KafkaProducerProperties {

    private Map<String, KafkaProducerProperties> configuration;

    public Producer buildProducer(String name) {

        KafkaProducerProperties targetProducerProperties = configuration.get(name);

        if (targetProducerProperties == null) {
            throw new IllegalArgumentException("can't found kafkaProducer properties. name=" + name);
        }

        KafkaConfig producerConfig = new KafkaConfig();

        Stream.of(targetProducerProperties, this).forEach(producerProperties -> {

            Optional.ofNullable(producerProperties.getConfig()).ifPresent(producerConfig::putAllAbsent);

            producerConfig.computeIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperties::getBootstrapServers);
            producerConfig.computeIfAbsent(ProducerConfig.CLIENT_ID_CONFIG, producerProperties::getClientId);
            producerConfig.computeIfAbsent(ProducerConfig.BATCH_SIZE_CONFIG, producerProperties::getBatchSize);
            producerConfig.computeIfAbsent(ProducerConfig.LINGER_MS_CONFIG, producerProperties::getLingerMs);
            producerConfig.computeIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerProperties::getKeySerializer);
            producerConfig.computeIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerProperties::getValueSerializer);
            producerConfig.computeIfAbsent(ProducerConfig.BUFFER_MEMORY_CONFIG, producerProperties::getBufferMemory);

        });

        KafkaProducer producer = new KafkaProducer(producerConfig);

        return producer;
    }
}
