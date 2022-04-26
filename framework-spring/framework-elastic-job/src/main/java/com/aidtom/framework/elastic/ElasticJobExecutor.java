package com.aidtom.framework.elastic;

/**
 * @author tom
 */
public interface ElasticJobExecutor {

    ElasticJobConfig getElasticJobConfig();

    void execute(ElasticJobTask task);

    boolean overwrite();
}
