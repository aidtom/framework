package com.aidtom.framework.threadpool;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池管理类
 *
 * @author tanghaihua
 * @date 2022/3/23
 */
@Configuration
@EnableConfigurationProperties(MultiThreadPoolConfig.class)
public class ThreadPoolConfiguration {

    @Bean
    @ConditionalOnProperty(value = {"framework.threadpool.use"}, havingValue = "true")
    public ThreadPoolTaskManager threadPoolTaskManager(MultiThreadPoolConfig cacheConfig) {
        return new ThreadPoolTaskManager(cacheConfig);
    }
}
