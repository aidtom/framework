package com.aidtom.framework.zookeeper.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.curator.framework.CuratorFramework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author tom
 * zookeeper:
 *   clusters:
 *     loadJobZk:
 *       url: mdczookee.zk.com:2181
 *       namespace: mdc/analysis
 *       max-retries: 3
 *       base-retry-sleep-time: 5000
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MultiZookeeperClientProperties extends ZookeeperClientProperties implements ZookeeperClientManager {

    public Map<String, ZookeeperClientProperties> clusters = new HashMap<>();

    @Override
    public CuratorFramework getClient(String cluster) {
        return Optional.ofNullable(clusters.get(cluster))
                .map(properties -> {
                    ZookeeperClientProperties merged = new ZookeeperClientProperties();
                    merged.setUrl(getOrDefault(properties, this, ZookeeperClientProperties::getUrl));
                    merged.setSessionTimeout(getOrDefault(properties, this, ZookeeperClientProperties::getSessionTimeout));
                    merged.setBaseRetrySleepTime(getOrDefault(properties, this, ZookeeperClientProperties::getBaseRetrySleepTime));
                    merged.setConnectionTimeout(getOrDefault(properties, this, ZookeeperClientProperties::getConnectionTimeout));
                    merged.setMaxRetries(getOrDefault(properties, this, ZookeeperClientProperties::getMaxRetries));
                    merged.setMaxCloseWait(getOrDefault(properties, this, ZookeeperClientProperties::getMaxCloseWait));
                    merged.setNamespace(getOrDefault(properties, this, ZookeeperClientProperties::getNamespace));
                    return merged;
                })
                .map(ZookeeperClientProperties::buildClient)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Zookeeper cluster: %s not found!", cluster)));
    }

    private <T, U> U getOrDefault(T exceptTarget, T defaultTarget, Function<T, U> valueExtractor) {
        U u = valueExtractor.apply(exceptTarget);
        if (u == null) {
            return valueExtractor.apply(defaultTarget);
        }
        return u;
    }
}
