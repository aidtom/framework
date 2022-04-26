package com.aidtom.framework.threadpool;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 初始化线程
 *
 * @author tom
 * @date 2022/3/23
 */
@Data
@Slf4j
@RequiredArgsConstructor
public class ThreadPoolTaskManager {
    private final MultiThreadPoolConfig config;
    private Map<String, ThreadPoolExecutor> threadPoolExecutorMap = new ConcurrentHashMap<>();

    /**
     * 获取线程池
     *
     * @return
     */
    public synchronized ThreadPoolExecutor getThreadPoolExecutor(String poolName) {
        return this.threadPoolExecutorMap.computeIfAbsent(poolName, t -> config.buildExecutor(poolName));
    }

}
