package com.aidtom.framework.kafka.config;

import lombok.Data;

import java.util.Map;

/**
 * kafka
 *
 * @author tom
 * @date 2020/11/25 17:35
 */
@Data
public class KafkaProducerProperties {

    private String bootstrapServers;

    private String clientId;

    private String keySerializer;

    private String valueSerializer;

    private String batchSize;

    private String bufferMemory;

    private String lingerMs;

    private Map<String, String> config;

}
