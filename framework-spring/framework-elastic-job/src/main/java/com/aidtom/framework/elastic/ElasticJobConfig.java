package com.aidtom.framework.elastic;

/**
 * @author tanghaihua
 */
public interface ElasticJobConfig {
    String getJobName();
    int getShardingTotal();
    String getCron();
}
