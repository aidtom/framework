package com.aidtom.framework.kafka.config;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * kafka
 *
 * @author tom
 * @date 2020/11/25 17:35
 */
@Data
public class KafkaConsumerProperties {

    private String topic;

    private String bootstrapServers;

    private List<String> topics;

    private String clientId;

    private String groupId;

    private String keyDeserializer;

    private String valueDeserializer;

    private String enableAutoCommit;

    private String autoCommitIntervalMs;

    private String fetchMinBytes;

    private String fetchMaxBytes;

    private Map<String, String> config;

    public List<String> getTopics() {

        Stream<String> topicStream = Stream.of(this.topic);

        if (!CollectionUtils.isEmpty(topics)) {
            topicStream = Stream.concat(topicStream, topics.stream());
        }

        return topicStream
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

}