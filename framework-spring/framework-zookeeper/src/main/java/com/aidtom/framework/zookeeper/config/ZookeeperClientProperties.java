package com.aidtom.framework.zookeeper.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.time.Duration;

/**
 * @author tanghaihua
 */
@Data
public class ZookeeperClientProperties {

    private String url;

    private String namespace;

    private Duration sessionTimeout;

    private Duration connectionTimeout;

    private Duration maxCloseWait;

    private Duration baseRetrySleepTime;

    private Integer maxRetries;

    public CuratorFramework buildClient() {
        if (StringUtils.isAllEmpty(url)) {
            throw new IllegalArgumentException("Zookeeper url can't be empty!");
        }
        if (StringUtils.isAllEmpty(namespace)) {
            throw new IllegalArgumentException("Zookeeper namespace can't be empty!");
        }
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(url)
                .namespace(namespace);
        if (sessionTimeout != null) {
            builder.sessionTimeoutMs((int) sessionTimeout.toMillis());
        }
        if (connectionTimeout != null) {
            builder.connectionTimeoutMs((int) connectionTimeout.toMillis());
        }
        if (maxCloseWait != null) {
            builder.maxCloseWaitMs((int) maxCloseWait.toMillis());
        }
        if (baseRetrySleepTime != null && maxRetries != 0) {
            builder.retryPolicy(new ExponentialBackoffRetry((int) baseRetrySleepTime.toMillis(), maxRetries));
        }
        return builder.build();
    }

}
