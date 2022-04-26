package com.aidtom.framework.elastic.config;

import com.aidtom.framework.elastic.ElasticJobExecutor;
import com.aidtom.framework.zookeeper.config.ZookeeperClientManager;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Map;


@Configuration
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "schedule.sharding-job", name = "mode", havingValue = "elastic-job")
@ConditionalOnBean({ElasticJobExecutor.class, ZookeeperClientManager.class})
@EnableConfigurationProperties(ElasticJobConfigurationProperties.class)
public class ElasticJobAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean("legionElasticJobAutoRegister")
    public ElasticJobAutoRegister elasticJobAutoRegister(ElasticJobConfigurationProperties properties,
                                                         Map<String, ElasticJobExecutor> shardingJobExecutors,
                                                         ZookeeperClientManager zookeeperClientManager) {
        return new ElasticJobAutoRegister(properties.buildRegistryCenter(applicationContext.getEnvironment(), zookeeperClientManager), shardingJobExecutors);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
