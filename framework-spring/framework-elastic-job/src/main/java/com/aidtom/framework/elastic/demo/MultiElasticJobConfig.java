package com.aidtom.framework.elastic.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author tanghaihua
 * @version 1.0
 * @description: MultiElasticJobProperties.java
 * @date 2021/12/20 4:57 下午
 */

@Data
@Component
@ConfigurationProperties(prefix = "convergence.elastic-job")
public class MultiElasticJobConfig {
    private Map<String, MinConvergenceElasticJobProperties> properties;
}
