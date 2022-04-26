package com.aidtom.framework.etcd;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Etcd 配置中心
 * @author tom
 */

@Configuration
@EnableConfigurationProperties(MultiEtcdProperties.class)
public class EtcdConfiguration {

    @Bean
    @ConditionalOnProperty(value = {"framework.etcd.use"}, havingValue = "true")
    public EtcdClientManager createEtcdManager(MultiEtcdProperties properties) {
        return new EtcdClientManager(properties);
    }
}
