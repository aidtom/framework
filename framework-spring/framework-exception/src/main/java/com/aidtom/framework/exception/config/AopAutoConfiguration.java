package com.aidtom.framework.exception.config;

import com.aidtom.framework.exception.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 保留类，如果控制器需要些aop在这里写
 *
 * @author tanghaihua
 * @date 2020/11/25 17:35
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopAutoConfiguration {
    /**
     * 默认的异常拦截器
     */
    @Bean
    public GlobalExceptionHandler globalControllerExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}