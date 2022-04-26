package com.aidtom.framework.threadpool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置
 *   framework:
 *     threadpool:
 *       use: true
 *       configuration:
 *         timLine:
 *           corePoolSize: 5
 *           maximumPoolSize: 2000
 *           keepAliveTime: 60
 *           capacity: 100000
 *
 * @author tanghaihua
 * @date 2022/3/23
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "framework.threadpool")
public class MultiThreadPoolConfig {
    private Map<String, ThreadPoolProperty> configuration;

    /**
     * 根据配置初始化线程池
     *
     * @return
     */
    public ThreadPoolExecutor buildExecutor(String poolName) {
        ThreadPoolProperty poolProperty;
        if (!configuration.containsKey(poolName)) {
            poolProperty = new ThreadPoolProperty();
            log.warn("Can't find threadPool executor name: " + poolName);
        } else {
            poolProperty = configuration.get(poolName);
        }
        return createThreadPoolExecutor(poolProperty, poolName);
    }

    /**
     * 创建线程池
     *
     * @param property
     * @param threadName
     * @return
     */
    private ThreadPoolExecutor createThreadPoolExecutor(ThreadPoolProperty property, String threadName) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(property.getCorePoolSize(), property.getMaximumPoolSize(), property.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(property.getCapacity()),
                new CustomThreadFactory(threadName),
                new ThreadPoolExecutor.AbortPolicy());
        //超时释放
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * 线程工厂
     */
    @Data
    private static class CustomThreadFactory implements ThreadFactory {
        private String poolName;
        private AtomicInteger count;

        private CustomThreadFactory(String poolName) {
            this.poolName = poolName;
            this.count = new AtomicInteger(0);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(poolName + "-[线程" + count.incrementAndGet() + "]");
            return thread;
        }
    }
}
