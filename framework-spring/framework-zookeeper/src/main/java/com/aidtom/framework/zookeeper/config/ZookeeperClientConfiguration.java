package com.aidtom.framework.zookeeper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tom
 */
@Configuration
public class ZookeeperClientConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "zookeeper")
    public MultiZookeeperClientProperties multiZookeeperClientProperties() {
        return new MultiZookeeperClientProperties();
    }
}
