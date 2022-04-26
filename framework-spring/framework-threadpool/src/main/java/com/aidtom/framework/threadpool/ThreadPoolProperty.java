package com.aidtom.framework.threadpool;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 线程池配置
 *
 * @author tanghaihua
 * @date 2022/3/23
 */
@Data
@Accessors(chain = true)
public class ThreadPoolProperty {
    private int corePoolSize = 5;
    private int maximumPoolSize = 2000;
    private long keepAliveTime = 60;
    private int capacity = 5000;
}
