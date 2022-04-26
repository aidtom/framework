package com.aidtom.framework.elastic;

/**
 * @author tanghaihua
 */
public interface ElasticJobExecutor {

    ElasticJobConfig getElasticJobConfig();

    void execute(ElasticJobTask task);

    boolean overwrite();
}
