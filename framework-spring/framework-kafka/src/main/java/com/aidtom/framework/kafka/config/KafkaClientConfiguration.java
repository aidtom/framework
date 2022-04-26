package com.aidtom.framework.kafka.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * kafka
 *
 * @author tanghaihua
 * @date 2020/11/25 17:35
 */
@Configuration
@EnableConfigurationProperties({MultiKafkaConsumerProperties.class, MultiKafkaProducerProperties.class})
public class KafkaClientConfiguration {

    @ConditionalOnClass(name = "org.apache.kafka.clients.KafkaClient")
    @ConditionalOnMissingBean(name = "kafkaClientManager")
    @Bean
    public KafkaClientManager kafkaClientManager(MultiKafkaConsumerProperties consumerProperties,
                                                 MultiKafkaProducerProperties producerProperties) {
        return new SimpleKafkaClientManager(consumerProperties, producerProperties);
    }
}
