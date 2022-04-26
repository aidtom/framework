package com.aidtom.framework.kafka.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * kafka
 *
 * @author tanghaihua
 * @date 2020/11/25 17:35
 */
@RequiredArgsConstructor
public class SimpleKafkaClientManager implements KafkaClientManager {

    private final Map<String, Consumer<?, ?>> consumerMap = new ConcurrentHashMap<>();
    private final Map<String, Producer<?, ?>> producerMap = new ConcurrentHashMap<>();

    private final MultiKafkaConsumerProperties multiKafkaConsumerProperties;
    private final MultiKafkaProducerProperties multiKafkaProducerProperties;

    @Override
    public <K, V> Consumer<K, V> getConsumer(String consumerName) {
        //noinspection unchecked
        return (Consumer<K, V>) consumerMap.computeIfAbsent(consumerName, multiKafkaConsumerProperties::buildConsumer);
    }

    @Override
    public <K, V> Producer<K, V> getProducer(String producerName) {
        //noinspection unchecked
        return (Producer<K, V>) producerMap.computeIfAbsent(producerName, multiKafkaProducerProperties::buildProducer);
    }

}
