package com.aidtom.framework.elastic.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;

@Data
public class ElasticJobZkConfig {


    /**
     * 连接Zookeeper服务器的列表.
     * 包括IP地址和端口号.
     * 多个地址用逗号分隔.
     * 如: host1:2181,host2:2181
     */
    private List<@NotBlank String> servers;

    /**
     * 命名空间.
     */
    private String namespace;

    /**
     * 等待重试的间隔时间的初始值.
     */
    private Duration baseSleep = Duration.ofSeconds(1);

    /**
     * 等待重试的间隔时间的最大值.
     */
    private Duration maxSleep = Duration.ofSeconds(3);

    /**
     * 最大重试次数.
     */
    @Positive
    private int maxRetries = 3;

    /**
     * 会话超时时间.
     */
    private Duration sessionTimeout = Duration.ofSeconds(10);

    /**
     * 连接超时时间.
     */
    private Duration connectionTimeout = Duration.ofSeconds(5);

    /**
     * 连接Zookeeper的权限令牌.
     * 缺省为不需要权限验证.
     */
    private String digest;

    /**
     * 使用的 legion zk cluster
     */
    private String cluster;

}
