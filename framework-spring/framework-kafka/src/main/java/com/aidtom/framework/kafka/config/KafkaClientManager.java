package com.aidtom.framework.kafka.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

/**
 * kafka
 *
 * @author tom
 * @date 2020/11/25 17:35
 */
public interface KafkaClientManager {

    <K, V> Consumer<K, V> getConsumer(String consumerName);

    <K, V> Producer<K, V> getProducer(String producerName);
}
