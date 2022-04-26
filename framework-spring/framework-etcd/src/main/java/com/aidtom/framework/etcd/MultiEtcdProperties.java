package com.aidtom.framework.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Etcd 配置文件
 * @author tom
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "framework.etcd")
public class MultiEtcdProperties extends EtcdProperties {

    private final Map<String, EtcdProperties> properties = new HashMap<>();

    public Client build(String name) {
        if (!properties.containsKey(name)) {
            throw new RuntimeException("can't find etcd properties, name: " + name);
        }
        return this.build(properties.get(name));
    }

    /**
     * 构建 Etcd client
     * */
    private Client build(EtcdProperties properties) {

        ClientBuilder builder = Client.builder();
        if (properties.getEndPoints() != null && properties.getEndPoints().length > 0) {
            builder.endpoints(properties.getEndPoints());
        } else if (this.endPoints != null && this.endPoints.length > 0) {
            builder.endpoints(this.endPoints);
        } else {
            throw new IllegalArgumentException("property endPoints is empty");
        }
        if (StringUtils.isNotEmpty(properties.getUser())) {
            builder.user(ByteSequence.from(properties.getUser(), StandardCharsets.UTF_8));
        } else if (StringUtils.isNotEmpty(user)) {
            builder.user(ByteSequence.from(user, StandardCharsets.UTF_8));
        }
        if (StringUtils.isNotEmpty(properties.getPassword())) {
            builder.password(ByteSequence.from(properties.getPassword(), StandardCharsets.UTF_8));
        } else if (StringUtils.isNotEmpty(password)) {
            builder.password(ByteSequence.from(password, StandardCharsets.UTF_8));
        }
        if (StringUtils.isNotEmpty(properties.getAuthority())) {
            builder.authority(properties.getAuthority());
        } else if (StringUtils.isNotEmpty(authority)) {
            builder.authority(authority);
        }
        if (properties.getKeepAliveTime() != null) {
            builder.keepaliveTimeMs(properties.getKeepAliveTime());
        } else if (keepAliveTime != null) {
            builder.keepaliveTimeMs(keepAliveTime);
        }
        if (properties.getKeepAliveTimeout() != null) {
            builder.keepaliveTimeoutMs(properties.getKeepAliveTimeout());
        } else if (keepAliveTimeout != null) {
            builder.keepaliveTimeoutMs(keepAliveTimeout);
        }
        if (properties.getRetryDelay() != null) {
            builder.retryDelay(properties.getRetryDelay());
        } else if (retryDelay != null) {
            builder.retryDelay(retryDelay);
        }
        if (properties.getKeepAliveWithoutCalls() != null) {
            builder.keepaliveWithoutCalls(properties.getKeepAliveWithoutCalls());
        } else if (keepAliveWithoutCalls != null) {
            builder.keepaliveWithoutCalls(keepAliveWithoutCalls);
        }
        if (StringUtils.isNotEmpty(properties.getRetryMaxDuration())) {
            builder.retryMaxDuration(properties.getRetryMaxDuration());
        } else if (StringUtils.isNotEmpty(retryMaxDuration)) {
            builder.retryMaxDuration(retryMaxDuration);
        }
        if (properties.getConnectTimeout() != null) {
            builder.connectTimeoutMs(properties.getConnectTimeout());
        } else if (connectTimeout != null) {
            builder.connectTimeoutMs(connectTimeout);
        }
        if (properties.getDiscovery() != null) {
            builder.discovery(properties.getDiscovery());
        } else if (discovery != null) {
            builder.discovery(discovery);
        }

        return builder.build();
    }

}
