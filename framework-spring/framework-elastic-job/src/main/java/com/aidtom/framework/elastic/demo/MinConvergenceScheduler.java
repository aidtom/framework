package com.aidtom.framework.elastic.demo;

import com.aidtom.framework.elastic.ElasticJobConfig;
import com.aidtom.framework.elastic.ElasticJobExecutor;
import com.aidtom.framework.elastic.ElasticJobTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 分钟收敛
 *
 * @author tom
 * @date 2022/3/11
 */

@Slf4j
@Component(value = "minConvergenceScheduler")
public class MinConvergenceScheduler implements ElasticJobExecutor {

    @Override
    public boolean overwrite() {
        return false;
    }

    @Resource
    private MultiElasticJobConfig multiElasticJobConfig;


    @Value("${convergence.enable:false}")
    private boolean enable;

    @Override
    public ElasticJobConfig getElasticJobConfig() {
        return multiElasticJobConfig.getProperties().get("minConvergenceScheduler");
    }


    @Override
    public void execute(ElasticJobTask elasticJobTask) {
        if (enable) {
            // TODO: 2022/4/26
        }
    }
}
