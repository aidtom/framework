package com.aidtom.framework.elastic;

/**
 * @author tom
 */
public interface ElasticJobConfig {
    String getJobName();
    int getShardingTotal();
    String getCron();
}
